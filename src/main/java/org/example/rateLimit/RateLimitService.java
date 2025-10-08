package org.example.rateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // local in-memory map: key -> Bucket
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // sample default: 100 requests per minute
    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    public boolean tryConsume(String key, long tokens) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());
        return bucket.tryConsume(tokens);
    }

    // Optional: expose remaining tokens for response headers, monitoring
    public long getAvailableTokens(String key) {
        Bucket bucket = buckets.get(key);
        return bucket == null ? 0L : bucket.getAvailableTokens();
    }
}