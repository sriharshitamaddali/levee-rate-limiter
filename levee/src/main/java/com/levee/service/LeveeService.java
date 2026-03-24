package com.levee.service;

import com.levee.contract.LeveeInitRequest;
import com.levee.contract.LeveeInitResponse;
import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.model.AlgorithmType;
import com.levee.model.LeveeConfig;
import com.levee.repository.LeveeConfigRepository;
import com.levee.service.algorithms.RateLimitStrategy;
import com.levee.service.algorithms.StrategyFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static com.levee.constants.LeveeConstants.DEFAULT_REFILL_RATE;
import static com.levee.constants.LeveeConstants.DEFAULT_WINDOW_SIZE;

@RequiredArgsConstructor
@Service
public class LeveeService {
    private static final Logger logger = LoggerFactory.getLogger(LeveeService.class);

    @Autowired
    private LeveeConfigRepository leveeConfigRepository;

    @Autowired
    private LeveeUtils leveeUtils;

    @Autowired
    private StrategyFactory leveeFactory;

    public LeveeInitResponse createConfig(
            LeveeInitRequest request
    ) {
        String key = leveeUtils.generateKey(request.getAppId(), request.getEntityName());
        logger.info("INFO :: Check if levee config exists for key :: {}",key);
        Optional<LeveeConfig> isConfig = leveeConfigRepository.findById(key);
        LeveeConfig config = isConfig.orElse(null);
        if(Objects.isNull(config)) {
            config = new LeveeConfig(
                    key,
                    Optional.ofNullable(request.getAlgorithmType()).orElse(AlgorithmType.FIXED_WINDOW),
                    Optional.ofNullable(request.getFixedSize()).orElse(DEFAULT_WINDOW_SIZE),
                    Optional.ofNullable(request.getRefillRate()).orElse(DEFAULT_REFILL_RATE)
            );

            config = leveeConfigRepository.save(config);
            logger.info("INFO :: Creating configuration for :: {}",key);
        }

        return new LeveeInitResponse(config.getKey());
    }

    public LeveeUsageResponse evaluateUsage(
            LeveeUsageRequest request
    ) {
        String key = leveeUtils.generateKey(request.getAppId(), request.getEntityName());
        Optional<LeveeConfig> isConfig = leveeConfigRepository.findById(key);
        if(isConfig.isEmpty()) {
            //throw exception
            logger.error("Error :: Redis config not found");
            throw new NoSuchElementException("Levee config not found");
        }
        AlgorithmType algorithmType = isConfig.get().getAlgorithmType();
        RateLimitStrategy algorithm = leveeFactory.getStrategy(algorithmType);
        return algorithm.evaluate(request, isConfig.get());
    }
}
