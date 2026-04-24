package org.lkvkn.gistrade.database.mapper.user;

import java.util.function.Function;

import org.lkvkn.gistrade.service.entity.UserGrpcDto;
import org.lkvkn.gistrade.database.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcDtoToEntityMapper implements Function<UserGrpcDto, User> {

    @Override
    public User apply(UserGrpcDto dto) {
        return User.builder()
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }
    
}
