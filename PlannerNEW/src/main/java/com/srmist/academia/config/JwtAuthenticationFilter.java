package com.srmist.academia.config;

import com.srmist.academia.repository.UserRepository;
import com.srmist.academia.util.JwtUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil = new JwtUtil();

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            String email = jwtUtil.extractEmail(token);

            userRepository.findByEmail(email).orElseThrow();

            User userDetails = new User(email, "", List.of());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendError(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            sendError(response, "Unsupported token", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            sendError(response, "Invalid token format", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (SignatureException e) {
            e.printStackTrace();
            sendError(response, "Invalid token signature", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            sendError(response, "Token is empty or null", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            sendError(response, "Invalid user email provided", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
