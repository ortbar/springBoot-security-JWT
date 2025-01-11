package com.example.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

// clase que nos va a proporcinar los metodos para trabajar con token. Dos atributos imptes
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private  String secretKey; // para firmar el méthodo

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    // generar token de acceso
    public String GenerateAccesToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // fecha de creacion del token en milisegundos...
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration))) // cuando expira? : el momento actual + el tiempo de expiracion seteado, que como es string, se parsea a Long.
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256) // coge el signatureKey y lo vuelve a encriptar
                .compact();
    }

    // validar el token de acceso
    public boolean IsTokenValid(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e){
            log.error("Token inválido, error: ".concat(e.getMessage()));
            return false;
        }
    }

    // obtener el username que viene en el payload del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);

    }



    // obtener un solo claim (xej el username del usuario que ha generado el token). // <T> GENERICO, investigar
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);


    }

    // obtener todos los claims del token. metodo que permita obtener las características del token
    // los claims son atributos de la parte "payload" del token

    //obtener los claims del token
    public Claims extractAllClaims(String token) {
        return   Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // obtener firma del token
    public Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // vamos a decodficar esta clave y despues vamos a volver a encriptarla en un algoritmo de encriptacion que nos sirva para firmar el token
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
