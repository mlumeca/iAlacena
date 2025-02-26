package com.luisa.iAlacena.security;

import com.luisa.iAlacena.security.jwt.access.JwtAuthenticationFilter;
import com.luisa.iAlacena.user.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found with username: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/user/register", "/user/register-admin", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/activate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/forgot-password", "/user/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/user/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/all").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/profile/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/recipe/create").authenticated()
                        .requestMatchers(HttpMethod.GET, "/recipe").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/recipe/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/recipe/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/recipe/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/{user_id}/favorites").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/user/{user_id}/favorites/{recipe_id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/{user_id}/favorites").authenticated()
                        .requestMatchers(HttpMethod.POST, "/user/{user_id}/inventory").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/{user_id}/inventory").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/user/{user_id}/inventory/{ingredient_id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/user/{user_id}/inventory/{ingredient_id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/user/{user_id}/shopping-cart").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/user/{user_id}/shopping-cart/item/{ingredient_id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/user/{user_id}/shopping-cart/{ingredient_id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/user/{user_id}/shopping-cart").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/{user_id}/shopping-cart").authenticated()
                        .requestMatchers(HttpMethod.POST, "/ingredient/create").authenticated()
                        .requestMatchers(HttpMethod.GET, "/ingredient").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/ingredient/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/ingredient/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/ingredient/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/category/create").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/category/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/category").authenticated()
                        .requestMatchers(HttpMethod.GET, "/category/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/category/*/subcategory").authenticated()
                        .requestMatchers(HttpMethod.POST, "/user/*/profile-picture").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/user/change-password").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/user/{id}/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/category/*/parent").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/category/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/*/profile-picture").authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}