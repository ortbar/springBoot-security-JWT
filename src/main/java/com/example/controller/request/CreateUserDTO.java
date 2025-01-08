package com.example.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data // Genera los getters y setters
@AllArgsConstructor // genera constructor con todos los param
@NoArgsConstructor  // genera constructor sin param
@Builder
public class CreateUserDTO {

    //AQUI vamos a tener los mismos atributos que nuestro UserEntity sin las anotaciones de Lombok
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
    private Set<String> roles;

}
