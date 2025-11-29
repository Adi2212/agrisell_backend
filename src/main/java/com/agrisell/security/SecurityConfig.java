package com.agrisell.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**", "/user/**", "/actuator/**", "/imagekit/auth").permitAll()
                        .requestMatchers("/products", "/products/{id}", "/orders/create").permitAll()
                        .requestMatchers("/categories/**").permitAll()

                        .requestMatchers("/products/add", "/products/update/**", "/products/delete/**")
                        .hasAnyAuthority("ROLE_FARMER","ROLE_ADMIN")
                        .requestMatchers("/categories/add", "/user/get")
                        .hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/user/address")
                        .hasAnyAuthority("ROLE_FARMER","ROLE_BUYER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> {})
                // 🔥 ADD JWT FILTER
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
