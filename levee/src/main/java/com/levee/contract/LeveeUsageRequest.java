package com.levee.contract;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeveeUsageRequest implements Serializable {
    @NotNull(message = "entity name must be provided")
    private String entityName;
    @NotNull(message = "app id must be provided")
    private String appId;
    private Long cost;
}
