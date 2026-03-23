package com.levee.service;

import com.levee.contract.LeveeUsageRequest;
import com.levee.model.LeveeUsage;
import com.levee.repository.LeveeUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

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

    public LeveeUsage createOrFetch(String key) {
        LeveeUsage usageRecord = fetch(key);
        if(Objects.isNull(usageRecord)) {
            usageRecord = leveeUsageRepository.save(new LeveeUsage(
                    key,
                    0,
                   System.currentTimeMillis()
            ));
        }

        return usageRecord;
    }


}
