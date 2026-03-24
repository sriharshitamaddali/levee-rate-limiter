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

@Component
public class FixedWindowStrategy implements RateLimitStrategy{
    @Autowired
    private LeveeUsageService leveeUsageService;

    @Autowired
    private LeveeUtils leveeUtils;

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.FIXED_WINDOW;
    }

    @Override
    public LeveeUsageResponse evaluate(LeveeUsageRequest request, LeveeConfig config) {
        String key = leveeUtils.generateKey(request.getAppId(), request.getEntityName());
        LeveeUsage usage = leveeUsageService.createOrFetch(key, config);
        LeveeUsageResponse response = new LeveeUsageResponse();
        long availableTokens = usage.getAvailableTokens() - 1;
        if(availableTokens < 0) {
            // throw 429 exception
            response.setAllowed(false);
            throw new RateLimitExceededException(response);
        }
        usage.setAvailableTokens(availableTokens);
        leveeUsageService.save(usage);

        response.setAllowed(true);
        return response;
    }
}
