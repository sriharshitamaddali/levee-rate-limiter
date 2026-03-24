package com.levee.resource;

import com.levee.contract.LeveeInitRequest;
import com.levee.contract.LeveeInitResponse;
import com.levee.contract.LeveeUsageRequest;
import com.levee.contract.LeveeUsageResponse;
import com.levee.service.LeveeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/levee")
public class LeveeResource {
    private static final Logger logger = LoggerFactory.getLogger(LeveeResource.class);

    @Autowired
    private LeveeService leveeService;

    @PostMapping("/init")
    public ResponseEntity<LeveeInitResponse> initiateLevee(
            @Valid @RequestBody LeveeInitRequest leveeInitRequest
    ) {
        logger.info("INFO :: Started init request");
        LeveeInitResponse response = leveeService.createConfig(leveeInitRequest);
        logger.info("INFO :: Completed init request");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request")
    public ResponseEntity<LeveeUsageResponse> processRequest(
            @Valid @RequestBody LeveeUsageRequest leveeUsageRequest
    ) {
        logger.info("INFO :: Start evaluate request");
        LeveeUsageResponse usageResponse = leveeService.evaluateUsage(leveeUsageRequest);
        logger.info("INFO :: Completed evaluate request");
        return ResponseEntity.ok(usageResponse);
    }
}
