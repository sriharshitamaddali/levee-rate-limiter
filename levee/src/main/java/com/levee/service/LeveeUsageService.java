package com.levee.service;

import com.levee.contract.LeveeUsageRequest;
import com.levee.model.AlgorithmType;
import com.levee.model.LeveeConfig;
import com.levee.model.LeveeUsage;
import com.levee.repository.LeveeUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

import static com.levee.constants.LeveeConstants.DEFAULT_USAGE_TTL;

@Service
@RequiredArgsConstructor
public class LeveeUsageService {
    @Autowired
    private LeveeUsageRepository leveeUsageRepository;

    public LeveeUsage save(LeveeUsage usageRequest) {
        LeveeUsage savedUsageRecord = leveeUsageRepository.save(usageRequest);

        return savedUsageRecord;
    }

    public LeveeUsage fetch(String key) {
        Optional<LeveeUsage> result = leveeUsageRepository.findById(key);

        return result.orElse(null);
    }

    public LeveeUsage createOrFetch(String key, LeveeConfig config) {
        LeveeUsage usageRecord = fetch(key);
        long ttl = config.getAlgorithmType() == AlgorithmType.TOKEN_BUCKET
                ? DEFAULT_USAGE_TTL * 7
                : DEFAULT_USAGE_TTL;
        if(Objects.isNull(usageRecord)) {
            usageRecord = new LeveeUsage(
                    key,
                   System.currentTimeMillis(),
                    config.getFixedSize(),
                    ttl
            );

            usageRecord = leveeUsageRepository.save(usageRecord);
        }

        return usageRecord;
    }


}
