package com.edulink.config;

import com.edulink.security.CustomUserDetailsService;
import com.edulink.security.JwtAuthenticationFilter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
   
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // ========== PUBLIC ENDPOINTS ==========
                
                // Authentication
                .requestMatchers("/api/auth/**").permitAll()
                
                // Speed Test
                .requestMatchers("/api/speedtest/**").permitAll()
                
                // Courses (all public)
                .requestMatchers("/api/courses/**").permitAll()
                
                // Documents - Public GET endpoints
                .requestMatchers(HttpMethod.GET, "/api/documents").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/documents/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/documents/course/{courseId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/documents/{documentId}/preview").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/documents/{documentId}/rating").permitAll()
                
                // Activities - Public GET
                .requestMatchers(HttpMethod.GET, "/api/activities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/activities/{id}").permitAll()
                
                // Users - Public GET (might want to restrict this)
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                // Forum - Public GET
                .requestMatchers(HttpMethod.GET, "/api/forum/threads").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/forum/threads/{id}").permitAll()

                // Actuator endpoints
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/acuator/info").permitAll()
                // ========== AUTHENTICATED ENDPOINTS ==========
                
                // Documents - Authenticated
                .requestMatchers(HttpMethod.GET, "/api/documents/{documentId}/download").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/documents/upload").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/documents/{documentId}/rate").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/documents/{id}").authenticated()
                
                // Activities - Authenticated
                .requestMatchers(HttpMethod.POST, "/api/activities").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/activities/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/activities/**").authenticated()

                // Forum - Authenticated
                .requestMatchers("/api/forum/**").authenticated()
                
                // ========== ROLE-BASED ENDPOINTS ==========
                
                // Admin only
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                
                // Teacher only
                .requestMatchers("/api/teacher/**").hasAuthority("TEACHER")
                
                // Default: all other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /***
     * Authentication Provider Bean
     * @return AuthenticationProvider   
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /***
     * Authentication Manager Bean
     * @param config
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /***
     * Password Encoder Bean
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /***
     * CORS Configuration Source Bean
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
