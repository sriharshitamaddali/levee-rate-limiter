package com.levee.service.algorithms;

import com.levee.model.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StrategyFactory {
    private final Map<AlgorithmType, RateLimitStrategy> strategies;

    public StrategyFactory(List<RateLimitStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        RateLimitStrategy::getAlgorithmType,
                        Function.identity()
                ));
    }

    public RateLimitStrategy getStrategy(AlgorithmType algorithmType) {
        return Optional.ofNullable(strategies.get(algorithmType))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No strategy found for: " + algorithmType));
    }
}
