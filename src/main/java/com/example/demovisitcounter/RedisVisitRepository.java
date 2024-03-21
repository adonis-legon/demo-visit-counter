package com.example.demovisitcounter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository("redis")
public class RedisVisitRepository implements VisitRepository {
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    private final String VISIT_HASH_KEY_NAME = "demo:visit";

    @Override
    public void incrementCounter(int visitId) {
        redisTemplate.opsForHash().increment(VISIT_HASH_KEY_NAME, String.valueOf(visitId), 1);
    }

}
