package com.rich.app.card.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.codec.Decoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;



    public String getUsernameFromJwt(String token){
      return extractOneClaim(token,claim -> claim.getSubject());
    }

    private Key getSignIngKey(){
        byte[] keysBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keysBytes);
    }
    private Claims extractAllClaims(String token){
       return Jwts.parserBuilder()
               .setSigningKey(getSignIngKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
    }
    public <T> T extractOneClaim(String token, Function<Claims,T>claimParser){
        final  Claims claims=extractAllClaims(token);
        return claimParser.apply(claims);
    }

//    public String accessToken(  Map<String, Object> extraClaims,
//                                UserDetails userDetails){
//        return generateToken(extraClaims,userDetails,jwtExpiration);
//    }
    public String refreshToken(UserDetails userDetails){
        return  generateToken(new HashMap<>(),userDetails,refreshExpiration);
    }

    public String generateToken(Map<String, Object>extractClaims, UserDetails userDetails,long expiresAt){
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiresAt))
                .signWith(getSignIngKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String accessToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails, jwtExpiration);
    }
    public boolean tokenIsValid(String token, UserDetails userDetails){
        final  String username= getUsernameFromJwt(token);
        return (username.equals(userDetails.getUsername()))&& isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractOneClaim(token,date->date.getExpiration());
    }

}
