package org.lkvkn.gistrade.users.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.users.model.User;

public class EntityToDtoMapperTest {

    private EntityToDtoMapper mapper;

    @BeforeEach
    public void beforeEach() {
        mapper = new EntityToDtoMapper();
    }

    @Test
    public void onNullPropsShouldThrow() {
        // Arrange
        User input = User.builder().build();
        // Act & Assert
        assertThrows(NullPointerException.class, 
                    () -> mapper.apply(input));
    }

    @Test
    public void onNotNullPropsShouldNotThrow() {
        // Arrange & Act & Assert
        assertDoesNotThrow(() -> {
            mapper.apply(User.builder()
                    .id(1L)
                    .firstName("null")
                    .lastName("null")
                    .patronimyc("null")
                    .username("null")
                    .password("null")
                    .createdAt(null)
                    .updatedAt(null)
                    .role(AppRole.USER)
                    .build());
        });
    }
}
