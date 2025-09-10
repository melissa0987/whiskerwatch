package com.example.whiskerwatch.demo.controller.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String role;
    private String customerType;
    private UserResponse user;
    
    // Constructor without user details
    public JwtResponse(String accessToken, String tokenType, Long userId, String email, String role, String customerType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.customerType = customerType;
    }
}