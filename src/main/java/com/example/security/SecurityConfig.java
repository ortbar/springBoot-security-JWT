package com.example.security;


import com.example.security.filters.JwtAuthenticationFilter;
import com.example.security.filters.JwtAuthorizationFilter;
import com.example.security.jwt.JwtUtils;
import com.example.service.UserDeatilsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// va a ser una clase de configuracion de spring
@Configuration
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;


    // inyectamos la implementacion de userDetailService y lo pasamos como parametro en el AutenticationManager
    @Autowired
    UserDeatilsServiceImpl userDeatilsService;

    // inyecatamos aqui el fitro de autorizacion
    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;


    //metodo que configuridad la cadena de filtros, la seguridad de la aplicacion

    // comportamiento de acceso a los endpoints y el manejo de la sesion
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,AuthenticationManager authenticationManager) throws Exception {

        //definimos el filtro creado en la configuracion de Seguridad. Requiere que inyectemos JwtUtils para pasarselo como parametro, asi que inyecta más arriba
        // no lo definimos como un Bean, porque le tenemos que setear varios atributos
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");


        return httpSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("hello").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter) // primer filtro que se ejecuta
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // se ejectua antes que el UsernamePassword.., primero que toddo se valida el token
                .build();
    }

    //usuario seteado en memoria creado para la autenticacion basica. lUEGO NO SIRVE
//    @Bean
//    UserDetailsService userDetailsService() {
//    // para crear un usuario en memoria "por ahora"
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("Alejandro")
//                .password("1234")
//                .roles()
//                .build());
//
//        return manager;
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // para que no se maneje todavia la encriptacion
    }

    // para que el usuario en memoria pueda funcionar, tiene que ser adminstrado por un objeto que administre la autenticacion en la aplicacion , que es el autenticationManager, que exige manejar un passworEncoder, que exige una contraseña encriptada para dejar pasar a ningun usuario que no tenga la password encriptada
    //requiere un password encoder, que definimos mas arriba
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDeatilsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}
