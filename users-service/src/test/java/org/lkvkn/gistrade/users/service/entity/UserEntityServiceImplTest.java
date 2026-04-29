package org.lkvkn.gistrade.users.service.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lkvkn.gistrade.users.exceptions.UserAlreadyExistsException;
import org.lkvkn.gistrade.users.exceptions.UserNotFoundException;
import org.lkvkn.gistrade.users.model.User;
import org.lkvkn.gistrade.users.repository.UserQueryRepository;
import org.lkvkn.gistrade.users.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserEntityServiceImplTest {

    @Mock
    private UserRepository repository;
    
    @Mock
    private UserQueryRepository queryRepository;

    @InjectMocks
    private UserEntityServiceImpl service;

    @Nested
    public class CreateTests {
        @Test
        public void onCreateUserWithNullIdReturnUserWithId() {
            // Arrange
            var testUser = User.builder().id(null).build();
            var userWithId = User.copy(testUser);
            userWithId.setId(1L);
            when(repository.save(testUser)).thenReturn(userWithId);
            // Act
            var created = service.create(testUser);
            // Assert
            assertEquals(userWithId, created);
            verify(repository, times(1)).save(testUser);
        }

        @Test
        public void onAlreadyExistsUsernameThrowError() {
            // Arrange
            String testUsername = "test";
            var testUser = User.builder().username(testUsername).build();
            when(repository.existsByUsername(testUsername)).thenReturn(true);
            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, 
                    () -> service.create(testUser));
            verify(repository, times(1)).existsByUsername(testUsername);
        }

        @Test
        public void onCreateNullThrowNullPointException() {
            // Act & Assert
            assertThrows(NullPointerException.class, 
                    () -> service.create(null));
        }
    }


    @Nested
    public class ReadTests {
        
        @Test
        public void onReadNullThrowNullPointException() {
            // Arrange 
            when(repository.findById(null)).thenReturn(null);
            // Act & Assert
            assertThrows(NullPointerException.class, 
                    () -> service.read(null));
            verify(repository, times(1)).findById(null);
        }

        @Test
        public void onReadIdThatNotExistsThrowUserNotExists() {
            // Arrange
            Long testId = 1L;
            when(repository.findById(testId)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> service.read(testId));
            verify(repository, times(1)).findById(testId);
        }

        @Test
        public void onReadExistsIdReturnRequiredUser() {
            // Arrange
            Long firstUserId = 1L;
            Long secondUserId = 2L;
            User firstUser = User.builder().id(firstUserId).build();
            User secondUser = User.builder().id(secondUserId).build();
            when(repository.findById(firstUserId)).thenReturn(Optional.of(firstUser));
            when(repository.findById(secondUserId)).thenReturn(Optional.of(secondUser));
            // Act
            User foundFirst = service.read(firstUserId);
            User foundSecond = service.read(secondUserId);
            // Assert
            assertDoesNotThrow(() -> service.read(firstUserId));
            assertDoesNotThrow(() -> service.read(secondUserId));
            assertEquals(foundFirst, firstUser);
            assertEquals(foundSecond, secondUser);
            assertEquals(firstUserId, foundFirst.getId());
            assertEquals(secondUserId, foundSecond.getId());
            verify(repository, times(2)).findById(firstUserId);
        }
    }    
}
