package com.bj.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.bj.security.authorization.DynamicWhitelistAuthorizationManager;
import com.bj.security.service.JWTService;
import com.bj.security.service.SecurityUserService;
import com.bj.security.serviceImpl.CacheUtilService;

import lombok.RequiredArgsConstructor;

@AutoConfiguration
@EnableScheduling
@RequiredArgsConstructor
public class SecurityAutoConfiguration {

    private final JWTService jwtService;
    private final SecurityUserService userService;
    private final CacheUtilService cacheUtilService;
    private final WhitelistProvider whitelistProvider;


    @Bean
    public FilterRegistrationBean<CRCFilter> crcFilterRegistration(
            @Value("${spring.application.name}") String serviceName
    ) {
        CRCFilter filter = new CRCFilter(serviceName, whitelistProvider);

        FilterRegistrationBean<CRCFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setEnabled(false);   // ❗ Prevent servlet auto-registration
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JWTAuthFilter> jwtFilterRegistration(
            @Value("${spring.application.name}") String serviceName
    ) {
        JWTAuthFilter filter = new JWTAuthFilter(
                serviceName,
                jwtService,
                userService,
                cacheUtilService,
                whitelistProvider
        );

        FilterRegistrationBean<JWTAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setEnabled(false);   // ❗ Prevent servlet auto-registration
        return registration;
    }

    // =====================================================================================
    //                            SECURITY FILTER CHAIN
    // =====================================================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            FilterRegistrationBean<CRCFilter> crcReg,
            FilterRegistrationBean<JWTAuthFilter> jwtReg,
            DynamicWhitelistAuthorizationManager dynamicAuthManager
    ) throws Exception {

        CRCFilter crcFilter = crcReg.getFilter();      // real filter instance
        JWTAuthFilter jwtFilter = jwtReg.getFilter();  // real filter instance

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().access(dynamicAuthManager)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .addFilterBefore(crcFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // =====================================================================================
    //                            CORS CONFIGURATION
    // =====================================================================================

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
