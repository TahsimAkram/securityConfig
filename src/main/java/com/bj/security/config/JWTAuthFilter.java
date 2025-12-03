package com.bj.security.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bj.security.dto.User;
import com.bj.security.service.JWTService;
import com.bj.security.service.SecurityUserService;
import com.bj.security.serviceImpl.CacheUtilService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthFilter extends OncePerRequestFilter {

    private final String serviceName;
    private final JWTService jwtService;
    private final SecurityUserService userService;
    private final CacheUtilService cacheUtilService;
    private final WhitelistProvider whitelistProvider;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JWTAuthFilter(
            String serviceName,
            JWTService jwtService,
            SecurityUserService userService,
            CacheUtilService cacheUtilService,
            WhitelistProvider whitelistProvider
    ) {
        this.serviceName = serviceName;
        this.jwtService = jwtService;
        this.userService = userService;
        this.cacheUtilService = cacheUtilService;
        this.whitelistProvider = whitelistProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        List<String> whitelist = whitelistProvider.getJWTWhitelistedURL(serviceName);

        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }

        return whitelist.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token → let request continue (AuthorizationManager will apply rules)
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String subject = jwtService.extractSubject(jwt);

        // Invalid or already authenticated
        if (!StringUtils.hasText(subject)
                || SecurityContextHolder.getContext().getAuthentication() != null) {

            filterChain.doFilter(request, response);
            return;
        }

        String userId = subject.split("~")[0];
        User user = (User) userService.userDetailsService().loadUserByUsername(userId);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String cachedToken = cacheUtilService.read(userId);

        if (jwt.equals(cachedToken) && jwtService.validateToken(jwt)) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {
            cacheUtilService.delete(userId);
        }

        filterChain.doFilter(request, response);
    }
}
