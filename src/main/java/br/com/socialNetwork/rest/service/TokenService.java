package br.com.socialNetwork.rest.service;

import br.com.socialNetwork.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class TokenService {

    private static final long expirationTime = 180000;
    private String key = "String";

    public String generateToken(){
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject("Teste Jwt")
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims decodeToken(String treatedToken) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(treatedToken)
                .getBody();
    }
}
