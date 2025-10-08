package org.example.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    @Retryable(value = { java.io.IOException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public String callExternal() throws Exception {
        // do HTTP call (RestTemplate / WebClient) that may throw IOException
        // if adding WebClient, set proper timeouts
        if (Math.random() < 0.7) {
            log.warn("Simulating transient failure");
            throw new java.io.IOException("Transient network issue");
        }
        return "ok";
    }

    // Optional recover method
    public String recover(java.io.IOException ex) {
        log.error("Recovering from external call failure: {}", ex.getMessage());
        return "fallback";
    }

    // Timeout wrapper using CompletableFuture
    public <T> T withTimeout(CompletableFuture<T> future, long timeoutMillis) throws Exception {
        try {
            return future.get(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException te) {
            throw new TimeoutException("operation timed out");
        }
    }
}