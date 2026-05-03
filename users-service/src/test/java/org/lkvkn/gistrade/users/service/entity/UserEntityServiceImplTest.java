package org.lkvkn.gistrade.users.service.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lkvkn.gistrade.common.enums.AppRole;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Nested
    public class ReadAllTests {
        
        @Test
        public void onNotExistUsersReturnEmptyList() {
            // Arrange
            when(repository.findAll()).thenReturn(new ArrayList<>());

            // Act
            List<User> actualList = service.readAll();

            // Assert
            assertTrue(actualList.isEmpty());
        }

        @Test
        public void onReadUsersReturnExists() {
            // Arrange
            User user = User.builder().build();
            when(repository.findAll()).thenReturn(List.of(user));

            // Act
            List<User> actualList = service.readAll();

            // Assert
            assertTrue(actualList.size() == 1);
            assertEquals(actualList.get(0), user);
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    public class UpdateTests {

        @Test
        public void onUpdateUserWithDifferentAndExistingUsernameShouldThrowException() {
            // Arrange
            Long userId = 1L;
            String currentUsername = "currentUser";
            String newButExistingUsername = "existingUser";
            
            User existingUser = User.builder()
                    .id(userId)
                    .username(currentUsername)
                    .firstName("Old")
                    .lastName("User")
                    .build();
            
            User updateDto = User.builder()
                    .id(userId)
                    .username(newButExistingUsername)  // Другой username
                    .firstName("New")
                    .lastName("User")
                    .password("newPass")
                    .role(AppRole.ADMIN)
                    .build();
            
            when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(repository.existsByUsername(newButExistingUsername)).thenReturn(true); // Важно!
            
            // Act & Assert
            assertThrows(UserAlreadyExistsException.class,
                    () -> service.updateFully(updateDto));
            
            // Проверяем, что save не вызывался
            verify(repository, never()).save(any());
            verify(repository, times(1)).existsByUsername(newButExistingUsername);
        }
        
        @Test
        public void onUpdateNullObjectThrowNullPointerException() {
            // Arrange & Act & Assert
            assertThrows(NullPointerException.class, 
                    () -> service.updateFully(null));
        }

        @Test
        public void onUpdateUserWithNoExistsIdThrowUserNotFoundException() {
            // Arrange
            Long testUserId = 1L;
            User testUser = User.builder().id(testUserId).build();
            when(repository.findById(testUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> service.updateFully(testUser));
        }

        @Test
        public void onUpdateExistsUserWithCorrectUsernameChangeAllProps() {
            // Arrange
            Long testUserId = 1L;
            String testUsername = "test";
            String expectedFirstName = "fn";
            String expectedLastName = "ln";
            String expectedPatronimyc = "pt";
            String expectedPassword = "pwd";

            User existsUser = User.builder()
                    .id(testUserId)
                    .username(testUsername)
                    .build();
            
            User expectedUser = User.builder()
                    .id(testUserId)
                    .username(testUsername)
                    .firstName(expectedFirstName)
                    .lastName(expectedLastName)
                    .patronimyc(expectedPatronimyc)
                    .password(expectedPassword)
                    .build();
            
            when(repository.findById(testUserId)).thenReturn(Optional.of(existsUser));
            when(repository.save(existsUser)).thenReturn(expectedUser);

            // Act
            User actual = service.updateFully(existsUser);

            // Assert
            assertEquals(expectedUser, actual);
        }

        @Test
        public void onUpdateAlreadyExistsThrowError() {
            // Arrange
            String existsUsername = "us1";
            Long existsUserId = 1L;
            User existsUser = User.builder()
                    .id(existsUserId)
                    .username("us2")
                    .build();            
            User userForUpdate = User.builder()
                    .id(existsUserId)
                    .username(existsUsername)
                    .build();
            when(repository.findById(existsUserId)).thenReturn(Optional.of(existsUser));
            when(repository.existsByUsername(existsUsername)).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class,
                    () -> service.updateFully(userForUpdate));
        }

        @Test
        public void onUpdateUserWithNullIdThrowError() {
            // Arrange
            User user = User.builder().id(null).build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                () -> service.updateFully(user));
        }

        
        @Test
        public void onUpdateUserWithDifferentAndNonExistingUsernameShouldUpdateSuccessfully() {
            // Arrange - покрывает ветку: username отличается И НЕ существует
            Long userId = 1L;
            String currentUsername = "currentUser";
            String newUsername = "newUser";
            
            User existingUser = User.builder()
                    .id(userId)
                    .username(currentUsername)
                    .firstName("Old")
                    .lastName("User")
                    .build();
            
            User updateDto = User.builder()
                    .id(userId)
                    .username(newUsername)
                    .firstName("New")
                    .lastName("User")
                    .password("pass")
                    .role(AppRole.USER)
                    .build();
            
            User expectedUser = User.builder()
                    .id(userId)
                    .username(newUsername)
                    .firstName("New")
                    .lastName("User")
                    .password("pass")
                    .role(AppRole.USER)
                    .build();
            
            when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(repository.existsByUsername(newUsername)).thenReturn(false);
            when(repository.save(existingUser)).thenReturn(expectedUser);
            
            // Act
            User result = service.updateFully(updateDto);
            
            // Assert
            assertEquals(expectedUser, result);
            verify(repository).existsByUsername(newUsername);
            verify(repository).save(existingUser);
        }
        
        @Test
        public void onUpdateUserWithSameUsernameShouldNotCheckExistence() {
            // Arrange - покрывает ветку: username НЕ отличается
            Long userId = 1L;
            String sameUsername = "sameUser";
            
            User existingUser = User.builder()
                    .id(userId)
                    .username(sameUsername)
                    .firstName("Old")
                    .build();
            
            User updateDto = User.builder()
                    .id(userId)
                    .username(sameUsername)  // Тот же username
                    .firstName("New")
                    .build();
            
            when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(repository.save(existingUser)).thenReturn(updateDto);
            
            // Act
            User result = service.updateFully(updateDto);
            
            // Assert
            assertEquals("New", result.getFirstName());
            // Важно: existsByUsername НЕ вызывается
            verify(repository, never()).existsByUsername(anyString());
            verify(repository).save(existingUser);
        }
        
        @Test
        public void onUpdateUserWithNullIdShouldThrowIllegalArgumentException() {
            // Arrange
            User user = User.builder().id(null).build();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, 
                    () -> service.updateFully(user));
        }

        @Test
        public void onUpdateUserWithNonExistentIdShouldThrowUserNotFoundException() {
            // Arrange
            Long testUserId = 1L;
            User testUser = User.builder().id(testUserId).build();
            when(repository.findById(testUserId)).thenReturn(Optional.empty());
            
            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> service.updateFully(testUser));
        }
    }

    @Nested
    public class DeleteTests  {
        
        @Test
        public void onDeleteNonExistsUserThrowError() {
            // Arrange
            Long id = 1L;
            // Act & Assert
            assertThrows(UserNotFoundException.class, 
                () -> service.delete(id));
        }

        @Test
        public void shouldDeleteExistsUser() {
            // Arrange
            Long id = 1L;
            when(repository.existsById(id)).thenReturn(true);

            // Act & Assert
            assertDoesNotThrow(() -> service.delete(id));
        }
    } 

    @Nested
    public class ReadByUsernameTests {
    
        @Test
        public void onReadExistsUsernameReturnCorrectUser() {
            // Arrange
            String testUsername = "exists";
            Long id = 1L;
            User expected = User.builder().id(id).username(testUsername).build();
            when(repository.findByUsername(testUsername)).thenReturn(Optional.of(expected));

            // Act
            User actual = service.readByUsername(testUsername);

            // Assert
            assertEquals(expected, actual);
        }    

        @Test
        public void onReadNonExistsUsernameShouldThrowError() {
            // Arrange & Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> service.readByUsername("test"));
        }
    }

    @Nested
    public class PartialUpdateTests {

        @Test
        public void onNotExistsIdShouldThrowError() {
            // Arrange & Act & Assert
            assertThrows(UserNotFoundException.class,
                () -> service.partialUpdate(1L, new HashMap<>()));
        }

        @Test
        public void onEmptyPropsEntityShouldNotChange() {
            // Arrange
            Long id = 1L;
            User existsUser = User.builder().id(id).build();
            when(repository.findById(id)).thenReturn(Optional.of(existsUser));
            when(repository.save(existsUser)).thenReturn(existsUser);

            // Act
            User actual = service.partialUpdate(id, new HashMap<>());
            
            // Assert
            assertEquals(existsUser, actual);
        }

        @Test
        public void onPropsEqualsNullShouldThrow() {
            // Arrange
            Long id = 1L;
            User exists = User.builder().id(id).build();
            when(repository.findById(id)).thenReturn(Optional.of(exists));

            // Act & Assert
            assertThrows(NullPointerException.class, 
                () -> service.partialUpdate(id, null));
        }

        @Test
        public void onRequestChangeAllPropsShouldChangeAllObject() {
            // Arrange
            Long id = 1L;
            User exists = User.builder().id(id).username("us2").build();
            String username = "us1";
            String firstName = "fn1";
            String lastName = "ln1";
            String patronimyc = "pt";
            String role = "USER";
            String password = "pwd";
            Map<String, String> props = new HashMap<>();
            props.put("username", username);
            props.put("role", role);
            props.put("first_name", firstName);
            props.put("last_name", lastName);
            props.put("patronimyc", patronimyc);
            props.put("password", password);
            User expected = User.builder()
                    .id(id)
                    .username(username)
                    .role(AppRole.USER)
                    .firstName(firstName)
                    .lastName(lastName)
                    .patronimyc(patronimyc)
                    .password(password)
                    .build();
            when(repository.findById(id)).thenReturn(Optional.of(exists));
            when(repository.save(exists)).thenReturn(expected);

            // Act
            User actual = service.partialUpdate(id, props);

            // Assert
            assertEquals(expected, actual);
            assertEquals(expected.getFirstName(), firstName);
            assertEquals(expected.getLastName(), lastName);
            assertEquals(expected.getPatronimyc(), patronimyc);
            assertEquals(expected.getRole(), AppRole.USER);
            assertEquals(expected.getPassword(), password);
            assertEquals(expected.getUsername(), username);
        }

        @Test
        public void onChangeNotExpectedPropShouldNotChangeObject() {
            // Arrange
            Long userId = 1L;
            User exists = User.builder().id(userId).build();
            Map<String, String> props = new HashMap<>();
            props.put("not_impl", "newVal");
            when(repository.save(any(User.class))).thenReturn(exists);
            when(repository.findById(userId)).thenReturn(Optional.of(exists));

            // Act
            User actual = service.partialUpdate(userId, props);

            // Assert
            assertEquals(exists, actual);
        }

        @Test
        public void onChangeToExistsUsernameShouldThrow() {
            // Arrange
            Long id = 1L;
            String username = "us1";
            String usernameRequest = "us2";
            User user = User.builder().id(id).username(username).build();
            when(repository.findById(id)).thenReturn(Optional.of(user));
            when(repository.existsByUsername(usernameRequest)).thenReturn(true);
            Map<String, String> propsRequest = new HashMap<>();
            propsRequest.put("username", usernameRequest);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class,
                    () -> service.partialUpdate(id, propsRequest));
        }

        @Test
        public void onChageToNotExistsUsernameShouldReturnUpdated() {
            // Arrange
            Long id = 1L;
            String currentUsername = "us1";
            String usernameRequest = "us2";
            User user = User.builder().id(id).username(currentUsername).build();
            User expected = User.builder().id(id).username(usernameRequest).build();
            Map<String, String> propsRequest = new HashMap<>();
            propsRequest.put("username", usernameRequest);
            when(repository.existsByUsername(usernameRequest)).thenReturn(false);
            when(repository.findById(id)).thenReturn(Optional.of(user));
            when(repository.save(user)).thenReturn(expected);

            // Act
            User actual = service.partialUpdate(id, propsRequest);
            
            // Assert
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class ReadByPropsTests {
        
        @Test
        public void onReadByNullObjectShouldThrow() {
            // Arrange & Act & Assert
            assertThrows(NullPointerException.class,
                    () -> service.readByProps(null));
        }

        @Test
        public void onReadByEmptyPropertiesShouldReturnEmptyList() {
            // Arrange
            Map<String, String> emptyProps = new HashMap<>();
            List<User> expectedList = new ArrayList<>();
            when(queryRepository.findByProperties(emptyProps)).thenReturn(expectedList);
            
            // Act
            List<User> result = service.readByProps(emptyProps);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(queryRepository, times(1)).findByProperties(emptyProps);
        }

        @Test
        public void onReadByPropertiesShouldReturnUserList() {
            // Arrange
            Map<String, String> props = Map.of("firstName", "John", "lastName", "Doe");
            List<User> expectedUsers = List.of(
                User.builder().firstName("John").lastName("Doe").build(),
                User.builder().firstName("John").lastName("Doe").build()
            );
            when(queryRepository.findByProperties(props)).thenReturn(expectedUsers);
            
            // Act
            List<User> result = service.readByProps(props);
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedUsers, result);
            verify(queryRepository, times(1)).findByProperties(props);
        }

        @Test
        public void onReadByPropertiesWithSingleParamShouldReturnFilteredList() {
            // Arrange
            Map<String, String> props = Map.of("role", "ADMIN");
            List<User> adminUsers = List.of(
                User.builder().role(AppRole.ADMIN).build(),
                User.builder().role(AppRole.ADMIN).build()
            );
            when(queryRepository.findByProperties(props)).thenReturn(adminUsers);
            
            // Act
            List<User> result = service.readByProps(props);
            
            // Assert
            assertEquals(2, result.size());
            result.forEach(user -> assertEquals(AppRole.ADMIN, user.getRole()));
        }

        @Test
        public void onReadByPropertiesWithNoMatchingResultsShouldReturnEmptyList() {
            // Arrange
            Map<String, String> props = Map.of("username", "nonexistent");
            when(queryRepository.findByProperties(props)).thenReturn(new ArrayList<>());
            
            // Act
            List<User> result = service.readByProps(props);
            
            // Assert
            assertTrue(result.isEmpty());
            verify(queryRepository, times(1)).findByProperties(props);
        }
    }

    @Nested
    public class ChangeUsernameTests {
        
        @Test
        public void onChangeToSameUsernameShouldNotCheckExistence() {
            // Arrange
            Long userId = 1L;
            String currentUsername = "sameUser";
            User user = User.builder().id(userId).username(currentUsername).build();
            Map<String, String> props = Map.of("username", currentUsername);
            
            when(repository.findById(userId)).thenReturn(Optional.of(user));
            when(repository.save(user)).thenReturn(user);
            
            // Act
            User result = service.partialUpdate(userId, props);
            
            // Assert
            assertEquals(currentUsername, result.getUsername());
            // Важно: existsByUsername НЕ вызывается
            verify(repository, never()).existsByUsername(anyString());
        }
        
        @Test
        public void onChangeToDifferentExistingUsernameShouldThrow() {
            // Arrange
            Long userId = 1L;
            String currentUsername = "currentUser";
            String takenUsername = "takenUser";
            User user = User.builder().id(userId).username(currentUsername).build();
            Map<String, String> props = Map.of("username", takenUsername);
            
            when(repository.findById(userId)).thenReturn(Optional.of(user));
            when(repository.existsByUsername(takenUsername)).thenReturn(true);
            
            // Act & Assert
            assertThrows(UserAlreadyExistsException.class,
                () -> service.partialUpdate(userId, props));
            
            // Проверяем, что username не изменился
            assertEquals(currentUsername, user.getUsername());
            verify(repository, never()).save(any());
        }
        
        @Test
        public void onChangeToDifferentNonExistingUsernameShouldUpdate() {
            // Arrange
            Long userId = 1L;
            String currentUsername = "currentUser";
            String newUsername = "newUser";
            User user = User.builder().id(userId).username(currentUsername).build();
            User expected = User.builder().id(userId).username(newUsername).build();
            Map<String, String> props = Map.of("username", newUsername);
            
            when(repository.findById(userId)).thenReturn(Optional.of(user));
            when(repository.existsByUsername(newUsername)).thenReturn(false);
            when(repository.save(user)).thenReturn(expected);
            
            // Act
            User result = service.partialUpdate(userId, props);
            
            // Assert
            assertEquals(newUsername, result.getUsername());
            verify(repository).existsByUsername(newUsername);
            verify(repository).save(user);
        }
    }
}
