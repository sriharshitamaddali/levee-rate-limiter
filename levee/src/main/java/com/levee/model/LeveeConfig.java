package com.levee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import static com.levee.constants.LeveeConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "levee:config", timeToLive = DEFAULT_CONFIG_TTL)
public class LeveeConfig {

    @Id
    private String key;
    private AlgorithmType algorithmType;  // enum: FIXED_WINDOW | SLIDING_WINDOW | TOKEN_BUCKET | LEAKY_BUCKET
    private long fixedSize = DEFAULT_WINDOW_SIZE;                // maximum limit
    private long refillRate = DEFAULT_REFILL_RATE; //refilling at 125 tokens/hour supporting 8 hour working day
}
