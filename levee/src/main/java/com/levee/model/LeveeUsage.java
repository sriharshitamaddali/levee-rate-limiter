package com.levee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import static com.levee.constants.LeveeConstants.DEFAULT_USAGE_TTL;

@Data
@NoArgsConstructor
@RedisHash(value = "levee:usage", timeToLive = DEFAULT_USAGE_TTL)
public class LeveeUsage {
    @Id
    private String key;
    private Long availableTokens; //this is for token bucket
    private long windowStart; // windowStart for fixed window and last refill time for token bucket

    @TimeToLive
    private long ttl;

    public LeveeUsage(
            String key,
            long windowStart,
            Long availableTokens,
            long ttl
    ) {
        this.key = key;
        this.windowStart = windowStart;
        this.availableTokens = availableTokens;
        this.ttl = ttl;
    }
}
