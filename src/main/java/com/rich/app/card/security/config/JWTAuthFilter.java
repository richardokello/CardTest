package com.rich.app.card.security.config;

import com.rich.app.card.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

 final String authHeader=request.getHeader("Authorisation");
 final String jwt;
 final String userEmail;

 if (authHeader==null|| !authHeader.startsWith("Bearer")){
     filterChain.doFilter(request,response);
     return;
 }

    jwt=authHeader.substring(7);
    userEmail=jwtService.getUsernameFromJwt(jwt);


 if (userEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null)
    {
     UserDetails userDetails=userDetailsService.loadUserByUsername(userEmail);

     var isTokenValid = tokenRepository.findByToken(jwt)
             .map(t -> !t.isExpired() && !t.isRevoked())
             .orElse(false);

     if (jwtService.tokenIsValid(jwt,userDetails)&&isTokenValid){
         UsernamePasswordAuthenticationToken userToken=
                 new UsernamePasswordAuthenticationToken(
                         userDetails,
                         null,
                 userDetails.getAuthorities());

         userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
         SecurityContextHolder.getContext().setAuthentication(userToken);
               }
           }
       filterChain.doFilter(request,response);
    }
}
