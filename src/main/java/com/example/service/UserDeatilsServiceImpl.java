package com.example.service;

import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

// clase personalizada UserDetailsService para recuperar los usuarios desde la basde de datos con su permisos y toddo y poder autenticarnos con los usuarios qu se encuentar en la bd
@Service
public class UserDeatilsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // recuperar el usuario de la bd
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario" + username + "no existe."));

        // coger los permisos del usuario
        Collection<? extends GrantedAuthority> authorities = userEntity.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())))
                .collect(Collectors.toSet());

        //retornamos un new User de springSecurity y con esto le estamos diciendo a security que el usuario que se va indentificar lo tiene que buscar en la bd
        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                true,
                true,
                true,
                true,
                authorities
                );
    }
}
