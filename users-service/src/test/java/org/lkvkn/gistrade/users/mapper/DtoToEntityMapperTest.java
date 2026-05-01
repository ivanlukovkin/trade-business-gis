package org.lkvkn.gistrade.users.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.service.users.UserGrpcDto;

public class DtoToEntityMapperTest {

    private DtoToEntityMapper mapper;

    @BeforeEach
    public void beforeEach() {
        mapper = new DtoToEntityMapper();
    }

    @Test
    public void onNullDtoShouldThrowNullPointException() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class,
                () -> mapper.apply(null));
    }

    @Test
    public void onEmptyRoleShouldSetUser() {
        // Arrange
        var dto = UserGrpcDto.newBuilder().setRole("").build();

        // Act
        var actual = mapper.apply(dto);

        // Assert
        assertEquals(AppRole.USER, actual.getRole());
    }

    @Test
    public void onCorrectRoleSholudMapValue() {
        // Arrange
        var dto = UserGrpcDto.newBuilder().setRole("USER").build();

        // Act
        var actual = mapper.apply(dto);

        // Assert
        assertEquals(AppRole.USER, actual.getRole());
    }
}
