package com.levee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import static com.levee.constants.LeveeConstants.DEFAULT_USAGE_TTL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "levee:usage", timeToLive = DEFAULT_USAGE_TTL)
public class LeveeUsage {
    @Id
    private String key;
    private long consumed;
    private long windowStart;
}
