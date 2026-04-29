package org.lkvkn.gistrade.common.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

import com.google.protobuf.Timestamp;

public class ProtobufTimestampMapper implements Function<LocalDateTime, Timestamp> {

    @Override
    public Timestamp apply(LocalDateTime localDateTime) {
        if (localDateTime == null) return Timestamp.getDefaultInstance();
        long epochSecond = localDateTime.toEpochSecond(ZoneOffset.UTC);
        Instant instant = Instant.ofEpochSecond(epochSecond, localDateTime.getNano());
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
    
}
