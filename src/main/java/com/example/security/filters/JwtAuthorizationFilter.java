package com.example.security.filters;

import com.example.security.jwt.JwtUtils;
import com.example.service.UserDeatilsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ESTE ES EL FILTRO PARA VALIDAR EL TOKEN. FILTRO DE AUTORIZACION

// se va a autenticar una vez por cada endpoint. nos obliga a q siempre  tenemos que enviar el token de adcceso para consumir el recurso. Se anota como Componente
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // lo necesitamos apra validar el token
    @Autowired
    private JwtUtils jwtUtils;

    // Vamos a necesitar consultar el usuario en la bd
    @Autowired
    private UserDeatilsServiceImpl userDeatilsService;



    // se anotan los parametros con @NonNull para asegurarnos que estos parametros SIEMPRE VENGAN
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // extraer el token de la peticion
        String tokenHeader = request.getHeader("Authorization");

        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            String token = tokenHeader.substring(7);

            if(jwtUtils.IsTokenValid(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                UserDetails userDetails = userDeatilsService.loadUserByUsername(username);

                //ahora,  una vez recuperado el usuario y los permisos que tiene, nos autenticamos...
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                // tb llamamos a este objeto, que contiene la autenticacion propia; es donde se guarda la autenticacion. accedemos a ella y la seteamos con el token de autenticacion
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

        }
        // si el token es null o no se inicia con Bearer, va a continuar con el filtro de validacion
        filterChain.doFilter(request, response);

    }
}
