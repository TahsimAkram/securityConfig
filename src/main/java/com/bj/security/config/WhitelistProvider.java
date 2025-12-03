package com.bj.security.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bj.security.repository.WhitelistRepository;
import com.bj.security.repository.WhitelistRepository.WhitelistData;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WhitelistProvider {

    private final WhitelistRepository whitelistRepository;
    private volatile int lastCount = -1;

    private volatile Map<String, List<String>> jwtAllowedUrlMap = Collections.emptyMap();
    private volatile Map<String, List<String>> crcAllowedUrlMap = Collections.emptyMap();

    @PostConstruct
    public void onApplicationReady() {
        refreshWhitelist();
    }

    public List<String> getJWTWhitelistedURL(String serviceName) {
        return jwtAllowedUrlMap.getOrDefault(serviceName, Collections.emptyList());
    }
    
    public List<String> getCRCWhitelistedURL(String serviceName) {
        return crcAllowedUrlMap.getOrDefault(serviceName, Collections.emptyList());
    }


    @Scheduled(fixedDelay = 300000) 
    public void refreshWhitelist() {
        try {
            int newCount = whitelistRepository.fetchTotalCount();

            if (newCount != lastCount) {
                System.out.println("[SECURITY] Whitelist count changed. Reloading...");

                WhitelistData allUrlsMap = whitelistRepository.fetchAllAsMap();
                Map<String, List<String>> updatedJWTUrlMap = allUrlsMap.jwtMap();
                Map<String, List<String>> updatedCRCUrlMap = allUrlsMap.crcMap();
                Map<String, List<String>> jwtMap = new HashMap<>();
                Map<String, List<String>> crcMap = new HashMap<>();
                
                updatedJWTUrlMap.forEach((service, urls) ->
                jwtMap.put(service, List.copyOf(urls))
                );

                updatedCRCUrlMap.forEach((service, urls) ->
                crcMap.put(service, List.copyOf(urls))
                );
                
                jwtAllowedUrlMap = Map.copyOf(jwtMap);
                crcAllowedUrlMap = Map.copyOf(crcMap);
                
                lastCount = newCount;

                System.out.println("[SECURITY] Whitelist reloaded. JWT Services: " + jwtAllowedUrlMap.keySet());
                System.out.println("[SECURITY] Whitelist reloaded. CRC Services: " + crcAllowedUrlMap.keySet());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}