package com.example.whiskerwatch.demo.security;

import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.repository.UserJPARepository;

 

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJPARepository userRepository;

    public CustomUserDetailsService(UserJPARepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true) // Spring's annotation
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUserPrincipal(user);
    }
    // Custom UserDetails implementation
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // Add role-based authority
            if (user.getRole() != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));
            }
            
            // Add customer type-based authority if exists
            if (user.getCustomerType() != null) {
                authorities.add(new SimpleGrantedAuthority("TYPE_" + user.getCustomerType().getTypeName()));
            }
            
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail(); // Using email as username
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.getIsActive() != null ? user.getIsActive() : true;
        }

        // Getter for the User entity
        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getId();
        }

        public String getRole() {
            return user.getRole() != null ? user.getRole().getRoleName() : null;
        }

        public String getCustomerType() {
            return user.getCustomerType() != null ? user.getCustomerType().getTypeName() : null;
        }
    }
}