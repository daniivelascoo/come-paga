package com.example.comepaga.model.user;

import com.example.comepaga.annotation.Encrypted;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @NonNull
    @Id
    @JsonProperty("id")
    private String nombreUsuario;

    @NonNull
    @JsonProperty("fecha_creacion")
    private LocalDate fechaCreacion;

    @NonNull
    @JsonProperty("nombre")
    private String nombre;

    @NonNull
    @JsonProperty("password")
    @Encrypted
    private String password;

    @NonNull
    @JsonProperty("primer_apellido")
    private String primerApellido;

    @JsonProperty("segundo_apellido")
    private String segundoApellido;

    @NonNull
    @JsonProperty("tipo_usuario")
    private String tipoUsuario;

    @NonNull
    @JsonProperty("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NonNull
    @JsonProperty("numero_telefono")
    private String numeroTelefono;
}
