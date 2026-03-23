package com.levee.contract;

import com.levee.model.AlgorithmType;
import com.levee.model.LeveeConfig;
import com.levee.model.RestrictionType;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeveeInitRequest implements Serializable {
    @NotNull(message = "entity name must be provided")
    private String entityName;
    @NotNull(message = "app id must be provided")
    private String appId;
    private AlgorithmType algorithmType;  // enum: FIXED_WINDOW | SLIDING_WINDOW | TOKEN_BUCKET | LEAKY_BUCKET -- default fixed_window
    @Positive @NotNull(message = "fixed size must be provided")
    private long fixedSize;                // maximum limit
}
