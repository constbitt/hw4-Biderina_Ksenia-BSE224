package org.hse.software.construction.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hse.software.construction.auth.config.JwtService;
import org.hse.software.construction.auth.model.session.SessionService;
import org.hse.software.construction.auth.model.token.Token;
import org.hse.software.construction.auth.model.token.TokenRepository;
import org.hse.software.construction.auth.model.token.TokenType;
import org.hse.software.construction.auth.model.user.Role;
import org.hse.software.construction.auth.model.user.User;
import org.hse.software.construction.auth.model.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final SessionService sessionService;
  private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
  public AuthenticationResponse register(RegisterRequest request) {

    if (request.getNickname().isEmpty()) {
      throw new IllegalArgumentException("Nickname must not be empty");
    }

    if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
      throw new IllegalArgumentException("Invalid email. Email must not be empty and contain @ and .");
    }

    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Another user is already registered under this email");
    }

    if (!isStrongPassword(request.getPassword())) {
      throw new IllegalArgumentException("The password must consist of at least 8 characters, including both case letters, numbers and special characters");
    }

    Role userRole = request.getRole() != null ? request.getRole() : Role.USER;
    var user = User.builder()
            .nickname(request.getNickname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .created(new Date())
            .role(userRole)
            .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    Date jwtExpiration = jwtService.extractExpiration(jwtToken);
    sessionService.createSession(savedUser, jwtToken, jwtExpiration);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
    } catch (Exception e) {
      throw new IllegalArgumentException("Incorrect email or password");
    }
      var user = repository.findByEmail(request.getEmail()).orElseThrow();
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      revokeAllUserTokens(user);
      saveUserToken(user, jwtToken);
      Date jwtExpiration = jwtService.extractExpiration(jwtToken);
      sessionService.createSession(user, jwtToken, jwtExpiration);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
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
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
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

  private boolean isStrongPassword(String password) {
    return PASSWORD_PATTERN.matcher(password).matches();
  }
}
