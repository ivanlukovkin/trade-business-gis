package org.lkvkn.gistrade.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceApplicationTest {

    @Test
    void testMainMethodShouldStartApplication() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // Arrange
            String[] args = new String[]{};
            
            // Act
            UsersServiceApplication.main(args);
            
            // Assert
            mockedSpringApplication.verify(
                () -> SpringApplication.run(UsersServiceApplication.class, args),
                times(1)
            );
        }
    }

    @Test
    void testMainMethodWithArguments() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            // Arrange
            String[] args = new String[]{"--server.port=8081", "--spring.profiles.active=dev"};
            
            // Act
            UsersServiceApplication.main(args);
            
            // Assert
            mockedSpringApplication.verify(
                () -> SpringApplication.run(UsersServiceApplication.class, args),
                times(1)
            );
        }
    }
}