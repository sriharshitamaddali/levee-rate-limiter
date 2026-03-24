package com.levee.service.algorithms;

import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.exception.RateLimitExceededException;
import com.levee.model.AlgorithmType;
import com.levee.model.LeveeConfig;
import com.levee.model.LeveeUsage;
import com.levee.service.LeveeUsageService;
import com.levee.service.LeveeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

import static com.levee.constants.LeveeConstants.DEFAULT_USAGE_TTL;

@Component
public class TokenBucketStrategy implements RateLimitStrategy{
    @Autowired
    private LeveeUsageService leveeUsageService;

    @Autowired
    private LeveeUtils leveeUtils;

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.TOKEN_BUCKET;
    }

    @Override
    public LeveeUsageResponse evaluate(LeveeUsageRequest request, LeveeConfig config) {
        long fixedSize = config.getFixedSize();
        Long cost = request.getCost();
        if(Objects.isNull(cost) || cost <= 0) {
            //throw Illegal Arguement exception
            throw new IllegalArgumentException(
                    "Cost must be provided and a positive value"
            );
        }

        /**
         * Steps:
         * 1. Get Available tokens for first request it is fixed_size and for subsequent it is carried forward
         * 2. Add refill tokens based on (current time - last refill time(window_start))/hours[float] * refill_rate
         * 3. new_available tokens will be min(fixed_size, available + refill)
         * 4. if cost > new_available_tokens 429 else new_available_tokens -= cost and allow
         * 5. save last_window_time and new_available_tokens
         * */
        String key = leveeUtils.generateKey(request.getAppId(), request.getEntityName());
        LeveeUsage usage = leveeUsageService.createOrFetch(key, config);
        LeveeUsageResponse response = new LeveeUsageResponse();
        long accumulatedTokens = usage.getAvailableTokens();
        long refillRate = config.getRefillRate();
        double elapsedHours = (System.currentTimeMillis() - usage.getWindowStart())/(1000.0 * 60.0 * 60.0);
        long refilledTokens = (long) (elapsedHours * refillRate);
        long availableTokens = Math.min(fixedSize, (accumulatedTokens + refilledTokens));
        if(cost > availableTokens) {
            // throw 429 exception
            response.setAllowed(false);
            throw new RateLimitExceededException(response);
        }
        availableTokens -= cost;
        usage.setAvailableTokens(availableTokens);
        usage.setWindowStart(System.currentTimeMillis());
        usage.setTtl(DEFAULT_USAGE_TTL * 7);
        leveeUsageService.save(usage);

        response.setAllowed(true);
        return response;
    }
}
