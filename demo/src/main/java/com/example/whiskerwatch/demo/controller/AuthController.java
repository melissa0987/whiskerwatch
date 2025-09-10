package com.example.whiskerwatch.demo.controller;

import com.example.whiskerwatch.demo.controller.request.LoginRequest;
import com.example.whiskerwatch.demo.controller.response.JwtResponse;
import com.example.whiskerwatch.demo.controller.response.UserResponse;
import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.security.CustomUserDetailsService;
import com.example.whiskerwatch.demo.security.JwtUtil;
import com.example.whiskerwatch.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(UserService userService, 
                         AuthenticationManager authenticationManager,
                         JwtUtil jwtUtil,
                         CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt - Email: " + loginRequest.getEmail()); // Debug log

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
                (CustomUserDetailsService.CustomUserPrincipal) userDetails;

            // Generate JWT token
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", userPrincipal.getUserId());
            extraClaims.put("role", userPrincipal.getRole());
            extraClaims.put("customerType", userPrincipal.getCustomerType());

            String jwt = jwtUtil.generateToken(userDetails, extraClaims);

            // Create response
            JwtResponse jwtResponse = new JwtResponse(
                jwt,
                "Bearer",
                userPrincipal.getUserId(),
                userPrincipal.getUsername(),
                userPrincipal.getRole(),
                userPrincipal.getCustomerType(),
                UserResponse.toResponse(userPrincipal.getUser())
            );

            return ResponseEntity.ok(jwtResponse);

        } catch (BadCredentialsException e) {
            System.out.println("Invalid credentials for email: " + loginRequest.getEmail()); // Debug log
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage()); // Debug log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Login failed. Please try again."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Invalid authorization header"));
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (username != null && jwtUtil.validateToken(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
                    (CustomUserDetailsService.CustomUserPrincipal) userDetails;

                // Generate new token
                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("userId", userPrincipal.getUserId());
                extraClaims.put("role", userPrincipal.getRole());
                extraClaims.put("customerType", userPrincipal.getCustomerType());

                String newToken = jwtUtil.generateToken(userDetails, extraClaims);

                JwtResponse jwtResponse = new JwtResponse(
                    newToken,
                    "Bearer",
                    userPrincipal.getUserId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getRole(),
                    userPrincipal.getCustomerType(),
                    UserResponse.toResponse(userPrincipal.getUser())
                );

                return ResponseEntity.ok(jwtResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Token refresh failed"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("valid", false, "message", "Invalid authorization header"));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                Optional<User> userOpt = userService.getUserByEmail(username);
                
                if (userOpt.isPresent()) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "user", UserResponse.toResponse(userOpt.get())
                    ));
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid or expired token"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token validation failed"));
        }
    }
}