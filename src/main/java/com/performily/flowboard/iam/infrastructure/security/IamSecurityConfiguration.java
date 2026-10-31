package com.performily.flowboard.iam.infrastructure.security;

import com.performily.flowboard.iam.application.internal.commandservices.IamService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class IamSecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, IamService iam) throws Exception {
        return http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers(
                        "/api/v1/auth/login",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll().anyRequest().authenticated())
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                            throws ServletException, IOException {
                        String h = req.getHeader("Authorization");
                        if (h != null && h.startsWith("Bearer ")) {
                            try {
                                var u = iam.valid(h.substring(7));
                                if (u.mustChangePassword
                                        && !req.getRequestURI().equals("/api/v1/auth/change-password")
                                        && !req.getRequestURI().equals("/api/v1/auth/logout")) {
                                    res.sendError(403, "Password change required");
                                    return;
                                }
                                var auth = new UsernamePasswordAuthenticationToken(
                                        u, 
                                        null, 
                                        List.of(new SimpleGrantedAuthority("ROLE_" + u.role.name()))
                                );
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            } catch (Exception e) {
                                res.sendError(401, "Invalid token");
                                return;
                            }
                        }
                        chain.doFilter(req, res);
                    }
                }, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}