package org.lkvkn.gistrade.api.mapper;

import java.util.function.Function;

import org.lkvkn.gistrade.api.model.UserWebDto;
import org.lkvkn.gistrade.common.mapper.LocalDateTimeMapper;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.springframework.stereotype.Service;

@Service
public class UsersWebDtoMapper implements Function<UserGrpcDto, UserWebDto> {

    private final LocalDateTimeMapper localDateTimeMapper = new LocalDateTimeMapper();

    @Override
    public UserWebDto apply(UserGrpcDto input) {
        return UserWebDto.builder()
                .id(input.getId())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .patronimyc(input.getPatronimyc())
                .username(input.getUsername())
                .password(input.getPassword())
                .role(input.getRole())
                .createdAt(localDateTimeMapper.apply(input.getCreatedAt()))
                .build();
    }

}
