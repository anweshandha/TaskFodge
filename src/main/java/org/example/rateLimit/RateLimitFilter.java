package org.example.rateLimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.TooManyRequestsException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Determine key: prefer authenticated user id (header or security context), fallback to remote IP.
        String userKey = extractKey(request);

        // check bucket
        boolean allowed = rateLimitService.tryConsume(userKey, 1);

        // set remaining tokens header (optional)
        long tokens = rateLimitService.getAvailableTokens(userKey);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(tokens));

        if (!allowed) {
            // Option A: short-circuit with 429
            throw new TooManyRequestsException("Rate limit exceeded for key: " + userKey);
            // Option B: set 429 directly (not throwing)
            // response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            // response.getWriter().write("Too many requests");
            // return;
        }

        // Optionally attach rate-limit info to MDC for logs
        MDC.put("rateLimitKey", userKey);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("rateLimitKey");
        }
    }

    private String extractKey(HttpServletRequest request) {
        // If you have JWT / authenticated user, extract username or user id
        String userHeader = request.getHeader("X-User-Id"); // example: client can pass header, or use SecurityContext
        if (userHeader != null && !userHeader.isEmpty()) {
            return "user:" + userHeader;
        }
        // fallback to IP
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(h -> h.split(",")[0].trim())
                .orElse(request.getRemoteAddr());
        return "ip:" + ip;
    }
}
