package com.levee.service.algorithms;

import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.exception.RateLimitExceededException;
import com.levee.model.AlgorithmType;
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
    public LeveeUsageResponse evaluate(LeveeUsageRequest request, long fixedSize) {
        String key = leveeUtils.generateKey(request.getAppId(), request.getEntityName());
        LeveeUsage usage = leveeUsageService.createOrFetch(key);
        String resetAt = leveeUtils.convertInstantToDateTime(usage.getWindowStart() + (24 * 60 * 60 * 1000L));
        LeveeUsageResponse response = new LeveeUsageResponse();
        response.setResetAt(resetAt);
        long consumed = usage.getConsumed() + 1;
        if(consumed > fixedSize) {
            // throw 429 exception
            response.setAllowed(false);
            response.setRemaining(0);
            response.setReason("Limit reached");
            throw new RateLimitExceededException(response);
        }
        usage.setConsumed(consumed);
        leveeUsageService.save(usage);
        long remaining = fixedSize - consumed;

        response.setAllowed(true);
        response.setRemaining(remaining);
        response.setReason("Allowed");

        return response;
    }
}
