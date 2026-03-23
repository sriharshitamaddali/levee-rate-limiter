package com.levee.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LeveeUtils {

    public String generateKey(String appId, String entityName) {
        return appId + ":" +entityName;
    }

    public String convertInstantToDateTime(
            long instantTime
    ) {
        Instant instant = Instant.ofEpochMilli(instantTime);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneOffset.UTC);

        return formatter.format(zonedDateTime);
    }
}
