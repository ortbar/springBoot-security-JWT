package com.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data // Genera los getters y setters
@AllArgsConstructor // genera constructor con todos los param
@NoArgsConstructor  // genera constructor sin param
@Builder // para construir objetos de esta clase
@Entity // para convertirlo en un entity de JPA
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // para generar automatiamente el id
    private Long id;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;

    // un usuario puede tener diferentes roles. un rol puede estar asociado a multiples usuarios, con lo cual muchos a muchos
    // la relacion solo se configura en user

    //configurar la relacion muchos a muchos y las claves foráneas
    @ManyToMany(fetch = FetchType.EAGER,targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    // como se configura la tabla intermedia que se genera entre userEntity y role y como se van a llamar las claves foraneas de userEntity y RoleEntity
    @JoinTable(name="user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roles; // por que set? set no permite tener elementos repetidos, list por el contrario si, entonces no nos sirve aquí.
}
