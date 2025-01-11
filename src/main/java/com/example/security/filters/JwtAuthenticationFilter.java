package com.example.security.filters;

// FILTRO PARA LA AUTENTICACION, CUANDO UN USUARIO SE VAYA A AUTENTICAR / REGISTRAR EN LA APLICACION

import com.example.models.UserEntity;
import com.example.security.jwt.JwtUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //creamos un constructor para poder usar JwtUtils, lo inyectamos por constructor, no por autowired
    private JwtUtils jwtUtils;
    // generar constructor con jwtutils como argumento
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // dos metodos principales, con generate overrideMethods..

    //intentar autenticarse
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //recuperar el usuario que se ha intentado autenticar
        UserEntity userEntity = null;
        String username= "";
        String password= "";

        // recuperamos usuario y pass que vienen en el request, pero viene en json y lo tenemos que mapear
        try {
            // toma los parametros de username y password y mapealos a una clase UserEntity
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            username = userEntity.getUsername();
            password = userEntity.getPassword();


        }  catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // objeto que se encarga de administrar la autenticacion...
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    // cuando se ha autenticado correctamente, qué es lo que queremos hacer???...generar el token
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {


        // para generar token 1ª obtener los detalles del usuario: usaurio - contrseña - roles con la clase user de springSecurity (estos datos vienen en el obj Authentication auth de la clase Authentication )
        // cast a User de springSecurity
        User user = (User) authResult.getPrincipal();
        String token = jwtUtils.GenerateAccesToken(user.getUsername());

        // en header de la respuesta enviamos tb el token
        response.addHeader("Authorization", token);

        // tb lo respondemos en el cuerpo de la respuesta, mapeamos la respuesta y la convertimos en un json
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("message:", "Autenticación Correcta");
        // usuario al que se le creo el token
        httpResponse.put("Username:", user.getUsername());
        // ponemos más parámetros a la respusta
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();



        super.successfulAuthentication(request, response, chain, authResult);
    }
}
