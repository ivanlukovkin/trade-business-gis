package org.lkvkn.gistrade.api.mapper;

import java.util.function.Function;

import org.lkvkn.gistrade.api.model.UserWebDto;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.springframework.stereotype.Service;

@Service
public class UsersGrpcDtoMapper implements Function<UserWebDto, UserGrpcDto> {

    @Override
    public UserGrpcDto apply(UserWebDto webDto) {
        if (webDto == null) {
            return null;
        }
        UserGrpcDto.Builder builder = UserGrpcDto.newBuilder()
            .setFirstName(webDto.getFirstName() != null ? webDto.getFirstName() : "")
            .setLastName(webDto.getLastName() != null ? webDto.getLastName() : "")
            .setPatronimyc(webDto.getPatronimyc() != null ? webDto.getPatronimyc() : "")
            .setUsername(webDto.getUsername() != null ? webDto.getUsername() : "")
            .setPassword(webDto.getPassword() != null ? webDto.getPassword() : "")
            .setRole(webDto.getRole() != null ? webDto.getRole() : "USER");
        Long id = webDto.getId();
        if (id != null) {
            builder.setId(id);
        }
        return builder.build();
    }

}
