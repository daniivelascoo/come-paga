package com.example.comepaga.service.impl;

import com.example.comepaga.annotation.interceptor.EncryptInterceptor;
import com.example.comepaga.model.user.AccessType;
import com.example.comepaga.model.user.Administrador;
import com.example.comepaga.model.user.Repartidor;
import com.example.comepaga.model.user.Usuario;
import com.example.comepaga.repo.CRUDRepository;
import com.example.comepaga.repo.UsuarioRepository;
import com.example.comepaga.repo.query.QueryBuilder;
import com.example.comepaga.service.UsuarioService;
import com.example.comepaga.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * The type Usuario service.
 */
@Service("UsuarioService")
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    /**
     * The Generic repo.
     */
    private final CRUDRepository<Usuario> genericRepo;
    /**
     * The Repository.
     */
    private final UsuarioRepository repository;

    /**
     * Instantiates a new Usuario service.
     *
     * @param genericRepo the generic repo
     * @param repository  the repository
     */
    @Autowired
    public UsuarioServiceImpl(
            @Qualifier("Mongo") CRUDRepository<Usuario> genericRepo,
            UsuarioRepository repository) {
        this.genericRepo = genericRepo;
        this.repository = repository;
    }

    @Override
    public ResponseEntity<Usuario> create(Usuario usuario, HttpServletRequest request) {
        if (genericRepo.exists(usuario.getNombreUsuario(), Usuario.class)) {
            log.warn("The username already exists {}", usuario.getNombreUsuario());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            if (this.detectRestrictedAccess(request, evaluateAccessForCreate(usuario, request)))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            log.info("Access accepted for create");
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return this.createUsuario(usuario);
    }

    @Override
    public ResponseEntity<Usuario> update(Usuario usuario, String id, HttpServletRequest request) {
        try {
            Optional<Usuario> opt = getUserCookieValue(request);

            if (opt.isPresent()) {
                if (! opt.get().getNombreUsuario().equals(id)) {
                    log.warn("A user has been detected trying to update another user");
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }

        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!usuario.getNombreUsuario().equals(id)) {
            log.info("The Usuario {} was changed because has different id {}", usuario, id);
            genericRepo.delete(id, Usuario.class);
        }

        Optional<Usuario> user = genericRepo.save(usuario);

        if (user.isPresent()) {
            log.info("User updated correctly: {}", user.get().getNombreUsuario());
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Usuario> get(String id) {
        Optional<Usuario> opt = genericRepo.findById(id, Usuario.class);

        return opt.map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Override
    public ResponseEntity<Void> delete(String id, HttpServletRequest request) {
        try {
            if (detectRestrictedAccess(request, AccessType.ADMINISTRADOR))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            log.info("Access accepted for delete");
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (genericRepo.delete(id, Usuario.class)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Usuario> login(String password, String name, HttpServletResponse response, HttpServletRequest request) {
        try {
            Optional<Usuario> userCookie = getUserCookieValue(request);

            if (userCookie.isPresent()) {
                log.info("An attempt will be made to log the User from the Cookie");
                password = userCookie.get().getPassword();
                name = userCookie.get().getNombreUsuario();
            }
        } catch (JsonProcessingException e) {
            log.info("Failed to log User in from Cookie");
        }

        if (Objects.isNull(password) || Objects.isNull(name)) {
            log.info("The login has been rejected");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Usuario> opt;
        try {
            opt = repository.findByNombreUsuarioAndPasswordLike(name, EncryptInterceptor.Encrypter.encrypt(password));
        } catch (NoSuchAlgorithmException e) {
            log.error("Error when trying to Encrypt the password.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return opt.map(usuario -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            try {
                String value = mapper.writeValueAsString(usuario);
                value = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

                Cookie cookie = new Cookie(Constants.COME_PAGA_COOKIE_KEY, value);
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
                log.info("Te Cookie was created correctly");
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                return new ResponseEntity<>(usuario, HttpStatus.BAD_REQUEST);
            }
        }).orElseGet(() -> {
            log.warn("The Usuario was not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @Override
    public ResponseEntity<List<Usuario>> getAll(Map<String, Object> filter) {
        var query = new QueryBuilder(filter);
        List<Usuario> result = genericRepo.findAll(query.build(), Usuario.class);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Usuario> createRepartidor(Repartidor repartidor) {
        return this.createUsuario(repartidor);
    }

    @Override
    public ResponseEntity<Usuario> createAdmin(Administrador body) {
        return this.createUsuario(body);
    }

    /**
     * Evaluate access for create access type.
     *
     * @param usuario the usuario
     * @param req     the req
     * @return the access type
     * @throws JsonProcessingException the json processing exception
     */
    private AccessType evaluateAccessForCreate(Usuario usuario, HttpServletRequest req) throws JsonProcessingException {
        AccessType accessResult = AccessType.RESTRICTED;
        Optional<Usuario> opt = getUserCookieValue(req);

        if (opt.isPresent()) {
            Usuario u = opt.get();

            switch (u.getTipoUsuario()) {
                case "0":
                    if (usuario.getTipoUsuario().equals("2")) accessResult = AccessType.ADMINISTRADOR;
                    break;
                case "1":
                    if (usuario.getTipoUsuario().equals("1")) accessResult = AccessType.CLIENTE;
                    break;
            }
        } else {
            if (usuario.getTipoUsuario().equals("1")) accessResult = AccessType.ACCESS_GRANTED;
        }

        return accessResult;
    }

    /**
     * Create usuario response entity.
     *
     * @param user the user
     * @return the response entity
     */
    private ResponseEntity<Usuario> createUsuario(Usuario user) {
        try {
            user.setPassword(EncryptInterceptor.Encrypter.encrypt(user.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Usuario> opt = genericRepo.save(user);

        if (opt.isPresent()) {
            log.info("The Usuario was created: {}", opt.get());
            return new ResponseEntity<>(opt.get(), HttpStatus.CREATED);
        }

        log.warn("The Usuario was not created: {}", user);
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

}
