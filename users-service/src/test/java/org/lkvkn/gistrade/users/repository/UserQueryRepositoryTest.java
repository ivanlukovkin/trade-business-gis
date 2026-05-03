package org.lkvkn.gistrade.users.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lkvkn.gistrade.users.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ExtendWith(MockitoExtension.class)
class UserQueryRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<User> criteriaQuery;

    @Mock
    private Root<User> root;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private UserQueryRepository userQueryRepository;

    private Map<String, String> properties;

    @BeforeEach
    void setUp() {
        properties = new HashMap<>();
    }

    @Test
    void findByProperties_WithValidUsername_ShouldReturnUserList() {
        // Arrange
        properties.put("username", "john_doe");
        User expectedUser = User.builder().username("john_doe").build();
        List<User> expectedUsers = List.of(expectedUser);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "john_doe")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
        verify(criteriaQuery).where(any(Predicate[].class));
    }

    @Test
    void findByProperties_WithMultipleProperties_ShouldCreateMultiplePredicates() {
        // Arrange
        properties.put("username", "john_doe");
        properties.put("firstName", "John");
        properties.put("lastName", "Doe");
        
        User expectedUser = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .build();
        List<User> expectedUsers = List.of(expectedUser);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate predicate1 = mock(Predicate.class);
        Predicate predicate2 = mock(Predicate.class);
        Predicate predicate3 = mock(Predicate.class);
        
        when(criteriaBuilder.equal(root.get("username"), "john_doe")).thenReturn(predicate1);
        when(criteriaBuilder.equal(root.get("firstName"), "John")).thenReturn(predicate2);
        when(criteriaBuilder.equal(root.get("lastName"), "Doe")).thenReturn(predicate3);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(criteriaQuery).where(any(Predicate[].class));
    }

    @Test
    void findByProperties_WithEmptyProperties_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = List.of(
            User.builder().username("user1").build(),
            User.builder().username("user2").build()
        );

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(criteriaQuery, never()).where(any(Predicate[].class));
    }

    @Test
    void findByProperties_WithNullValue_ShouldSkipPredicate() {
        // Arrange
        properties.put("username", null);
        properties.put("email", null);
        
        List<User> expectedUsers = List.of(User.builder().username("user1").build());

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(criteriaBuilder, never()).equal(any(), any());
    }

    @Test
    void findByProperties_WithEmptyStringValue_ShouldSkipPredicate() {
        // Arrange
        properties.put("username", "");
        properties.put("email", "   ");
        
        List<User> expectedUsers = List.of(User.builder().username("user1").build());

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(criteriaBuilder, never()).equal(any(), any());
    }

    @Test
    void findByProperties_WithSinglePropertyAndEmptyList_ShouldReturnEmptyList() {
        // Arrange
        properties.put("username", "nonexistent");

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "nonexistent")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByProperties_WhenExceptionOccurs_ShouldReturnEmptyList() {
        // Arrange
        properties.put("username", "john_doe");

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "john_doe")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenThrow(new RuntimeException("Database error"));

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByProperties_WithMixedValidAndInvalidFields_ShouldFilterCorrectly() {
        // Arrange
        properties.put("username", "john_doe");
        properties.put("invalid_field", "");
        properties.put("first_name", "   ");
        
        User expectedUser = User.builder().username("john_doe").build();
        List<User> expectedUsers = List.of(expectedUser);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "john_doe")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
        verify(root, never()).get("invalid_field");
    }

    @Test
    void findByProperties_WithDatabaseQueryException_ShouldReturnEmptyList() {
        // Arrange
        properties.put("username", "john_doe");

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "john_doe")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        doThrow(new RuntimeException("Query execution failed")).when(typedQuery).getResultList();

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByProperties_WithSpecialCharactersInValue_ShouldHandleCorrectly() {
        // Arrange
        properties.put("username", "john_doe@example.com");
        User expectedUser = User.builder().username("john_doe@example.com").build();
        List<User> expectedUsers = List.of(expectedUser);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("username"), "john_doe@example.com")).thenReturn(mockPredicate);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe@example.com", result.get(0).getUsername());
    }

    @Test
    void findByProperties_WithMultipleValidProperties_ShouldCombineWithAnd() {
        // Arrange
        properties.put("first_name", "John");
        properties.put("last_name", "Doe");
        
        User expectedUser = User.builder().firstName("John").lastName("Doe").build();
        List<User> expectedUsers = List.of(expectedUser);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        
        Predicate predicate1 = mock(Predicate.class);
        Predicate predicate2 = mock(Predicate.class);
        
        when(criteriaBuilder.equal(root.get("firstName"), "John")).thenReturn(predicate1);
        when(criteriaBuilder.equal(root.get("lastName"), "Doe")).thenReturn(predicate2);
        
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryRepository.findByProperties(properties);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(criteriaQuery).where(any(Predicate[].class));
    }
}