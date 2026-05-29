package com.blognest.services;

public interface RateLimiterService {
    boolean tryConsume(String key, int limit, long durationSeconds);
}
