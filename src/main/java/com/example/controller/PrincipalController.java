package com.example.controller;

import com.example.controller.request.CreateUserDTO;
import com.example.models.ERole;
import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;


@RestController
public class PrincipalController {

    // inyectamos el passwordEnconder
    @Autowired
    private PasswordEncoder passwordEncoder;

    // llamar a UserRepository
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World NOT SECURED!!";
    }

    @GetMapping("/helloSecured")
    public String helloSecured() {
        return "Hello World  SECURED";
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {

        // 1 CREAR USUAURIO
        // recibimos un set de string con los nombres de los roles y lo estamos convirtiendo a un set RoleEntity para poder insertarlo a la db
        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                    .name(ERole.valueOf(role))
                    .build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                // encripta la password
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .roles(roles)
                .build();


        //tras crear el usuario, lo persistimos, lo enviamos a la bd
        userRepository.save(userEntity);


        // respondemos enviando el responseEntity ya creado
        return ResponseEntity.ok(userEntity);
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id) {
        userRepository.deleteById(Long.parseLong(id));
        return "Se ha borrado el user con id".concat(id);
    }



}
