package com.levee.exception;

import com.levee.contract.LeveeUsageResponse;

public class RateLimitExceededException extends RuntimeException {
    private final LeveeUsageResponse response;

    public RateLimitExceededException(LeveeUsageResponse response) {
        super("Rate limit exceeded");
        this.response = response;
    }

    public LeveeUsageResponse getResponse() {
        return response;
    }
}
