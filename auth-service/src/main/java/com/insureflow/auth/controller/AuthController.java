package com.insureflow.auth.controller;

import com.insureflow.auth.dto.AuthResponse;
import com.insureflow.auth.dto.LoginRequest;
import com.insureflow.auth.dto.RegisterRequest;
import com.insureflow.auth.dto.TokenValidationResponse;
import com.insureflow.auth.dto.UserProfileResponse;
import com.insureflow.auth.entity.Role;
import com.insureflow.auth.entity.User;
import com.insureflow.auth.repository.UserRepository;
import com.insureflow.auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(AuthResponse.builder().token(jwt).username(user.getUsername()).email(user.getEmail()).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Role.MEMBER);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .build());

    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> currentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();

        return ResponseEntity.ok(UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build());
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(TokenValidationResponse.builder().valid(false).build());
        }

        String token = authorizationHeader.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (RuntimeException ex) {
            return ResponseEntity.ok(TokenValidationResponse.builder().valid(false).build());
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !jwtService.validateToken(token, user)) {
            return ResponseEntity.ok(TokenValidationResponse.builder().valid(false).build());
        }

        return ResponseEntity.ok(TokenValidationResponse.builder()
                .valid(true)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build());
    }
}
