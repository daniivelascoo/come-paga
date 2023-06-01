package com.example.comepaga.repo;

import com.example.comepaga.model.user.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The interface Usuario repository.
 */
@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    /**
     * Find by nombre usuario and password like optional.
     *
     * @param id       the id
     * @param password the password
     * @return the optional
     */
    Optional<Usuario> findByNombreUsuarioAndPasswordLike(String id, String password);

}
