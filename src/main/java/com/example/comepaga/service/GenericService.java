package com.example.comepaga.service;

import com.example.comepaga.model.user.AccessType;
import com.example.comepaga.model.user.Usuario;
import com.example.comepaga.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * The interface Generic service.
 *
 * @param <T> the type parameter
 */
@Service
public interface GenericService<T> {

    /**
     * Create response entity.
     *
     * @param object  the object
     * @param request the request
     * @return the response entity
     */
    ResponseEntity<T> create(T object, HttpServletRequest request);

    /**
     * Update response entity.
     *
     * @param object  the object
     * @param id      the id
     * @param request the request
     * @return the response entity
     */
    ResponseEntity<T> update(T object, String id, HttpServletRequest request);

    /**
     * Get response entity.
     *
     * @param id the id
     * @return the response entity
     */
    ResponseEntity<T> get(String id);

    /**
     * Delete response entity.
     *
     * @param id      the id
     * @param request the request
     * @return the response entity
     */
    ResponseEntity<Void> delete(String id, HttpServletRequest request);

    /**
     * Check access boolean.
     *
     * @param req          the req
     * @param requiredType the required type
     * @return the boolean
     * @throws JsonProcessingException the json processing exception
     */
    default boolean detectRestrictedAccess(HttpServletRequest req, AccessType... requiredType) throws JsonProcessingException {
        if (Arrays.asList(requiredType).contains(AccessType.RESTRICTED)) return true;
        if (Arrays.asList(requiredType).contains(AccessType.ACCESS_GRANTED)) return false;

        Optional<Usuario> opt = getUserCookieValue(req);

        if (opt.isPresent()) {
            Usuario u = opt.get();
            for (AccessType type: requiredType) {
                if (u.getTipoUsuario().equals(type.getValue())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets user cookie value.
     *
     * @param req the req
     * @return the user cookie value
     * @throws JsonProcessingException the json processing exception
     */
    default Optional<Usuario> getUserCookieValue(HttpServletRequest req) throws JsonProcessingException {
        Cookie[] cookies = req.getCookies();

        if (Objects.isNull(cookies)) return Optional.empty();
        Optional<Cookie> cookie = Arrays.stream(cookies)
                .filter(val -> val.getName().equals(Constants.COME_PAGA_COOKIE_KEY))
                .findFirst();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        if (cookie.isPresent()) {
            String value = cookie.get().getValue();
            byte[] decodedBytes = Base64.getDecoder().decode(value);
            value = new String(decodedBytes, StandardCharsets.UTF_8);
            return Optional.of(mapper.readValue(value, Usuario.class));
        }

        return Optional.empty();
    }
}
