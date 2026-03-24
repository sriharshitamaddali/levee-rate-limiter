package com.levee.service.algorithms;

import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.model.AlgorithmType;
import com.levee.model.LeveeConfig;

public interface RateLimitStrategy {
    AlgorithmType getAlgorithmType();
    LeveeUsageResponse evaluate(LeveeUsageRequest leveeUsageRequest, LeveeConfig config);
}
