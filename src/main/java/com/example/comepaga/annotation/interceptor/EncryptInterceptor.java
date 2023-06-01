package com.example.comepaga.annotation.interceptor;

import com.example.comepaga.annotation.Encrypted;
import com.example.comepaga.model.user.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * The type Encrypt interceptor.
 */
@Component
public class EncryptInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object requestBody = request.getAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
        if (requestBody instanceof Usuario) {
            encryptPassword((Usuario) requestBody);
        }
        return true;
    }

    /**
     * Encrypt password.
     *
     * @param usuario the usuario
     */
    private void encryptPassword(Usuario usuario) {
        Class<?> clazz = usuario.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Encrypted.class) && field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    String password = (String) field.get(usuario);
                    if (password != null) {
                        String encryptedPassword = Encrypter.encrypt(password);
                        field.set(usuario, encryptedPassword);
                    }
                } catch (IllegalAccessException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * The type Encrypter.
     */
    public static class Encrypter {
        /**
         * Encrypt string.
         *
         * @param password the password
         * @return the string
         */
        public static String encrypt(String password) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Calcular el valor hash del texto de entrada
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convertir los bytes del hash a una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        }
    }
}
