package com.bj.security.authorization;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.bj.security.config.WhitelistProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DynamicWhitelistAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final WhitelistProvider whitelistProvider;

    @Value("${spring.application.name}")
    private String serviceName;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {

        HttpServletRequest request = context.getRequest();
        String path = request.getServletPath();

        List<String> whitelist = whitelistProvider.getJWTWhitelistedURL(serviceName);

        boolean whitelisted =
                whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (whitelisted) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authentication.get();

        boolean isAuthenticated =
                auth != null &&
                !(auth instanceof AnonymousAuthenticationToken) &&
                auth.isAuthenticated();

        return new AuthorizationDecision(isAuthenticated);
    }
}
