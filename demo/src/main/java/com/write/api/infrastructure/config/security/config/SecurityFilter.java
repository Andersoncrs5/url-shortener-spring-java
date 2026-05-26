package com.write.api.infrastructure.config.security.config;

import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.ports.out.repository.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final IUserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = tokenService.validateToken(token);

            if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            boolean exists = this.userRepository.existsByEmailIgnoreCase(email);

            if (!exists) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User '{}' authenticated successfully.", email);

            filterChain.doFilter(request, response);

        } catch (ResponseStatusException e) {
            log.warn("Authentication failed ({}): {}", e.getStatusCode(), e.getReason());
            SecurityContextHolder.clearContext();

            response.setStatus(e.getStatusCode().value());
            response.getWriter().write(e.getReason());
        } catch (Exception e) {
            log.warn("Fail in authentication JWT for a request: {}", e.getMessage());
            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);
        }
    }

    public String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.replace("Bearer ", "");
    }

}
