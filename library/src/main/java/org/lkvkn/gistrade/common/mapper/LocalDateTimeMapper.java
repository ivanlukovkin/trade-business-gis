package org.lkvkn.gistrade.common.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

import com.google.protobuf.Timestamp;

public class LocalDateTimeMapper implements Function<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime apply(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
