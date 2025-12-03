package com.bj.security.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.CRC32;
import java.util.List;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.bj.security.dto.GenericRS;
import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CRCFilter extends OncePerRequestFilter {

    private final String serviceName;
    private final WhitelistProvider whitelistProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String CRC_HEADER = "X-CRCTOKEN";
    private final Gson gson = new Gson();

    public CRCFilter(String serviceName, WhitelistProvider whitelistProvider) {
        this.serviceName = serviceName;
        this.whitelistProvider = whitelistProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        List<String> whitelist = whitelistProvider.getCRCWhitelistedURL(serviceName);

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

        // Only for write operations
        if (!List.of("POST", "PUT", "DELETE").contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String incomingCrc = request.getHeader(CRC_HEADER);
        String calculatedCrc;

        CachedBodyHttpServletRequest wrappedRequest = null;

        // -------------------- MULTIPART HANDLING --------------------
        if (request.getContentType() != null &&
            request.getContentType().toLowerCase().startsWith("multipart/")) {

            StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

            Map<String, Object> entries = new TreeMap<>();

            multipartRequest.getParameterMap()
                    .forEach((key, values) -> {
                        if (values != null && values.length > 0) {
                            entries.put(key, values[0]);
                        }
                    });

            multipartRequest.getMultiFileMap()
                    .forEach((key, files) -> {
                        if (!files.isEmpty()) {
                            MultipartFile file = files.get(0);
                            entries.put(key, file.getOriginalFilename());
                            entries.put("FILE_SIZ", file.getSize());
                        }
                    });

            String metaJson = gson.toJson(entries);
            calculatedCrc = getCRC32Hex(metaJson);

            if (incomingCrc == null || !incomingCrc.equals(calculatedCrc)) {
                sendError(response);
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        // -------------------- JSON / RAW PAYLOAD HANDLING --------------------
        wrappedRequest = new CachedBodyHttpServletRequest(request);
        String body = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        calculatedCrc = getCRC32Hex(body);

        if (incomingCrc == null || !incomingCrc.equals(calculatedCrc)) {
            sendError(response);
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    private void sendError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                gson.toJson(GenericRS.builder().msg("Invalid CRC Token").build())
        );
    }

    public static String getCRC32Hex(String jsonPayload) {
        CRC32 crc = new CRC32();
        byte[] bytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
        crc.update(bytes, 0, bytes.length);
        return Long.toHexString(crc.getValue());
    }
}
