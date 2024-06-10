package org.hse.software.construction.auth.controller.info;

import io.swagger.v3.oas.annotations.Hidden;
import org.hse.software.construction.auth.config.JwtService;
import org.hse.software.construction.auth.model.user.User;
import org.hse.software.construction.auth.model.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
@RequestMapping("/auth-api/user-info")
@Hidden
public class UserInfoController {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public UserInfoController(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @GetMapping
  public ResponseEntity<String> getInfo(@RequestHeader("Authorization") String token) {
    try {
      String email = jwtService.extractUsername(token.substring(7));
      User user = userRepository.findByEmail(email).orElseThrow();
      String nickname = user.getNickname();
      Date created = user.getCreated();
      return ResponseEntity.ok("Nickname: " + nickname + "\nEmail: " + email + "\nCreated: " + created);
    } catch (Exception e) {
      return ResponseEntity.status(404).body("Invalid token or user not found");
    }
  }
}
