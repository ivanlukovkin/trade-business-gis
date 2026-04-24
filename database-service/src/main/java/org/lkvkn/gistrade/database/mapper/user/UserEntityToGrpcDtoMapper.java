package org.lkvkn.gistrade.database.mapper.user;

import java.util.function.Function;

import org.lkvkn.gistrade.service.entity.UserGrpcDto;
import org.lkvkn.gistrade.database.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserEntityToGrpcDtoMapper implements Function<User, UserGrpcDto> {

    @Override
    public UserGrpcDto apply(User entity) {
        if (entity == null) return null;
        return UserGrpcDto.newBuilder()
                .setId(entity.getId())
                .setFullName(entity.getFullName())
                .setUsername(entity.getUsername())
                .setPassword(entity.getPassword())
                .setRole(entity.getRole())
                .build();
    }
    
}