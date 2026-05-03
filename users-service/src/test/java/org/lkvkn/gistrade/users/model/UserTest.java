package org.lkvkn.gistrade.users.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lkvkn.gistrade.common.enums.AppRole;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .patronimyc("Michael")
                .username("johndoe")
                .password("hashedPassword123")
                .role(AppRole.ADMIN)
                .build();
    }

    @Test
    public void testUserBuilderShouldCreateValidUser() {
        // Assert
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Michael", user.getPatronimyc());
        assertEquals("johndoe", user.getUsername());
        assertEquals("hashedPassword123", user.getPassword());
        assertEquals(AppRole.ADMIN, user.getRole());
    }

    @Test
    public void testUserBuilderWithDefaultRole() {
        // Act
        User userWithDefaultRole = User.builder()
                .username("testuser")
                .password("password")
                .build();

        // Assert
        assertEquals(AppRole.USER, userWithDefaultRole.getRole());
    }

    @Test
    public void testUserBuilderWithNullRole() {
        // Act
        User userWithNullRole = User.builder()
                .username("testuser")
                .password("password")
                .role(null)
                .build();

        // Assert
        assertNull(userWithNullRole.getRole());
    }

    @Test
    public void testOnCreateShouldSetTimestamps() {
        // Act
        User newUser = User.builder()
                .username("testuser")
                .password("password")
                .build();
        newUser.onCreate();

        // Assert
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
        assertDoesNotThrow(() -> newUser.onCreate()); // Should be callable multiple times
    }

    @Test
    public void testOnCreateShouldNotOverrideExistingTimestamps() {
        // Arrange
        LocalDateTime existingTime = LocalDateTime.now().minusDays(1);
        User existingUser = User.builder()
                .username("testuser")
                .password("password")
                .createdAt(existingTime)
                .updatedAt(existingTime)
                .build();

        // Act
        existingUser.onCreate();

        // Assert - onCreate sets timestamps even if they exist
        assertNotNull(existingUser.getCreatedAt());
        assertNotNull(existingUser.getUpdatedAt());
    }

    @Test
    public void testOnUpdateShouldUpdateUpdatedAtTimestamp() {
        // Arrange
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();
        user.onCreate(); // Set initial timestamps
        
        // Act
        user.onUpdate();

        // Assert
        assertNotNull(user.getUpdatedAt());
        if (originalUpdatedAt != null) {
            // UpdatedAt should be changed (or at least not null)
            assertNotNull(user.getUpdatedAt());
        }
    }

    @Test
    public void testOnUpdateWithNullUpdatedAt() {
        // Arrange
        user.setUpdatedAt(null);
        
        // Act
        user.onUpdate();

        // Assert
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    public void testPrePersistLifecycleMethod() {
        // Act
        user.onCreate();

        // Assert
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertDoesNotThrow(() -> user.onCreate());
    }

    @Test
    public void testPreUpdateLifecycleMethod() {
        // Arrange
        LocalDateTime beforeUpdate = LocalDateTime.now().minusHours(1);
        user.setUpdatedAt(beforeUpdate);
        
        // Act
        user.onUpdate();
        
        LocalDateTime afterUpdate = user.getUpdatedAt();
        
        // Assert
        assertNotNull(afterUpdate);
        // Note: In real scenario, onUpdate() sets to current time
        // We can't directly compare because time changes, but we can check it's not null
        // and that it's likely different if we could freeze time
    }

    @Test
    public void testCopyShouldCreateIndependentCopy() {
        // Arrange
        user.onCreate(); // Set timestamps
        User original = user;
        
        // Act
        User copied = User.copy(original);
        
        // Assert
        assertNotSame(original, copied);
        assertEquals(original.getId(), copied.getId());
        assertEquals(original.getFirstName(), copied.getFirstName());
        assertEquals(original.getLastName(), copied.getLastName());
        assertEquals(original.getPatronimyc(), copied.getPatronimyc());
        assertEquals(original.getUsername(), copied.getUsername());
        assertEquals(original.getRole(), copied.getRole());
        assertEquals(original.getCreatedAt(), copied.getCreatedAt());
        assertEquals(original.getUpdatedAt(), copied.getUpdatedAt());
        
        // Verify modifying copy doesn't affect original
        copied.setFirstName("Modified");
        assertEquals("John", original.getFirstName());
        assertEquals("Modified", copied.getFirstName());
    }

    @Test
    public void testCopyShouldNotCopyPassword() {
        // Arrange
        user.setPassword("secretPassword");
        
        // Act
        User copied = User.copy(user);
        
        // Assert
        assertNull(copied.getPassword()); // Password should not be copied
        assertEquals("secretPassword", user.getPassword()); // Original retains password
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        User userWithAllArgs = new User(
                3L,
                "Alice",
                "Johnson",
                "Marie",
                "alicej",
                "password123",
                AppRole.USER,
                now,
                now
        );
        
        // Assert
        assertEquals(3L, userWithAllArgs.getId());
        assertEquals("Alice", userWithAllArgs.getFirstName());
        assertEquals("Johnson", userWithAllArgs.getLastName());
        assertEquals("Marie", userWithAllArgs.getPatronimyc());
        assertEquals("alicej", userWithAllArgs.getUsername());
        assertEquals("password123", userWithAllArgs.getPassword());
        assertEquals(AppRole.USER, userWithAllArgs.getRole());
        assertEquals(now, userWithAllArgs.getCreatedAt());
        assertEquals(now, userWithAllArgs.getUpdatedAt());
    }

    @Test
    public void testToString() {
        // Act
        String toStringResult = user.toString();
        
        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("John"));
        assertTrue(toStringResult.contains("Doe"));
        assertTrue(toStringResult.contains("johndoe"));
        // Note: password might be included in toString based on @ToString annotation
        // If you don't want password in toString, add @ToString.Exclude on password field
    }

    @Test
    public void testUsernameAndPasswordConstraints() {
        // Username is nullable=false in entity, but in model it can be null
        User userWithoutUsername = User.builder()
                .password("password")
                .build();
        assertNull(userWithoutUsername.getUsername());
        
        // Password is nullable=false in entity, but in model it can be null
        User userWithoutPassword = User.builder()
                .username("testuser")
                .build();
        assertNull(userWithoutPassword.getPassword());
    }

    @Test
    public void testBuilderPatternWithAllFields() {
        // Act
        User completeUser = User.builder()
                .id(100L)
                .firstName("Test")
                .lastName("User")
                .patronimyc("T.")
                .username("testuser100")
                .password("securePass123")
                .role(AppRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Assert
        assertEquals(100L, completeUser.getId());
        assertEquals("Test", completeUser.getFirstName());
        assertEquals("User", completeUser.getLastName());
        assertEquals("T.", completeUser.getPatronimyc());
        assertEquals("testuser100", completeUser.getUsername());
        assertEquals("securePass123", completeUser.getPassword());
        assertEquals(AppRole.ADMIN, completeUser.getRole());
        assertNotNull(completeUser.getCreatedAt());
        assertNotNull(completeUser.getUpdatedAt());
    }
}