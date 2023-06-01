package com.example.comepaga.model.user;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "usuarios")
@Data
public class Administrador extends Usuario {
}
