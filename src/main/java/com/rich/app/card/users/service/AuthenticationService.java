package com.rich.app.card.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rich.app.card.security.config.JwtService;
import com.rich.app.card.security.token.Token;
import com.rich.app.card.security.token.TokenRepository;
import com.rich.app.card.security.token.TokenType;
import com.rich.app.card.users.models.ApplicationUser;
import com.rich.app.card.users.models.AuthenticationResponse;
import com.rich.app.card.users.models.Roles;
import com.rich.app.card.users.models.dao.AuthenticationRequest;
import com.rich.app.card.users.models.dao.RegisterRequest;
import com.rich.app.card.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
private final  PasswordEncoder passwordEncoder;
   private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private  final TokenRepository tokenRepository;





    public AuthenticationResponse register(RegisterRequest request) {
        var user = ApplicationUser.builder()
                .description(request.getDescription())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(Roles.MEMBER)
                .build();

      var savedUser=  repository.save(user);
      var accessToken= jwtService.accessToken(user);
      var refreshToken = jwtService.refreshToken(user);
      saveUserToken(savedUser,accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                request.getEmail()
                ,request.getPassword())
        );

        var user =repository.findByEmail(request.getEmail()).orElseThrow();
        var accessToken= jwtService.accessToken(user);
        var refreshToken=jwtService.refreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private void saveUserToken(ApplicationUser user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(ApplicationUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }




    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.getUsernameFromJwt(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.tokenIsValid(refreshToken, user)) {
                var accessToken = jwtService.accessToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
