package com.levee.service.algorithms;

import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.model.AlgorithmType;

public interface RateLimitStrategy {
    AlgorithmType getAlgorithmType();
    LeveeUsageResponse evaluate(LeveeUsageRequest leveeUsageRequest, long fixedSize);
}
