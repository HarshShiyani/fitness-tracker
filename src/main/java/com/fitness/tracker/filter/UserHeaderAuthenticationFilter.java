package com.fitness.tracker.filter;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class UserHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public UserHeaderAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userIdHeader = request.getHeader("X-USER-ID");

        if (userIdHeader != null) {
            try {
                Long userId = Long.valueOf(userIdHeader);
                User user = userService.getUser(userId);

                if (user != null) {
                    var authority = "ROLE_" + user.getRole().name();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, Collections.singleton(() -> authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
