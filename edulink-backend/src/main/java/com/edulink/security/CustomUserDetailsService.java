package com.edulink.security;

import com.edulink.model.User;
import com.edulink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // ✅ CREAR AUTHORITY CON EL ROL DEL USUARIO
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(user.getRole().name())  // "ADMIN", "STUDENT", "TEACHER"
        );

        System.out.println("=== LOADING USER DETAILS ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role: " + user.getRole().name());
        System.out.println("Authorities: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}