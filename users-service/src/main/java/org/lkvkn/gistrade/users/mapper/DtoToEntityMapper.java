package org.lkvkn.gistrade.users.mapper;

import java.util.function.Function;

import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.lkvkn.gistrade.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class DtoToEntityMapper implements Function<UserGrpcDto, User> {

	@Override
	public User apply(UserGrpcDto dto) {
        String roleStr = dto.getRole();
        AppRole role = AppRole.USER;
        if (!(roleStr == null || roleStr.trim().isEmpty())) {
            role = AppRole.valueOf(roleStr);
        }
        return User.builder()
            .id(dto.getId())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .patronimyc(dto.getPatronimyc())
            .username(dto.getUsername())
            .password(dto.getPassword())
            .role(role)
            .build();
	}

}
