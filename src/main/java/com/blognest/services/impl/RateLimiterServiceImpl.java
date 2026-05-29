package com.blognest.services.impl;

import com.blognest.services.RateLimiterService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private static class Bucket {
        private final long limit;
        private final long durationMs;
        private double tokens;
        private long lastRefillTime;

        public Bucket(long limit, long durationSeconds) {
            this.limit = limit;
            this.durationMs = durationSeconds * 1000;
            this.tokens = limit;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            if (elapsed > 0) {
                double refillAmount = (elapsed * (double) limit) / durationMs;
                tokens = Math.min(limit, tokens + refillAmount);
                lastRefillTime = now;
            }
        }
    }

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean tryConsume(String key, int limit, long durationSeconds) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(limit, durationSeconds));
        return bucket.tryConsume();
    }
}
