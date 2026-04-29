package org.lkvkn.gistrade.users.mapper;

import java.util.function.Function;

import org.lkvkn.gistrade.common.mapper.ProtobufTimestampMapper;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.lkvkn.gistrade.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class EntityToDtoMapper implements Function<User, UserGrpcDto> {

    private final ProtobufTimestampMapper timestampMapper = new ProtobufTimestampMapper();

    @Override
    public UserGrpcDto apply(User entity) {
        return UserGrpcDto.newBuilder()
                .setId(entity.getId())
                .setFirstName(entity.getFirstName())
                .setLastName(entity.getLastName())
                .setPatronimyc(entity.getPatronimyc())
                .setUsername(entity.getUsername())
                .setPassword(entity.getPassword())
                .setCreatedAt(timestampMapper.apply(entity.getCreatedAt()))
                .setUpdatedAt(timestampMapper.apply(entity.getUpdatedAt()))
                .setRole(entity.getRole().name())
                .build();
    }

}
