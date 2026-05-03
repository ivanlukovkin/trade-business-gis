package org.lkvkn.gistrade.users.service.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lkvkn.gistrade.service.users.AllUsersResponse;
import org.lkvkn.gistrade.service.users.FindUserByIdRequest;
import org.lkvkn.gistrade.service.users.FindUserByPropsRequest;
import org.lkvkn.gistrade.service.users.PartialUpdateRequest;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.lkvkn.gistrade.users.exceptions.UserNotFoundException;
import org.lkvkn.gistrade.users.mapper.DtoToEntityMapper;
import org.lkvkn.gistrade.users.mapper.EntityToDtoMapper;
import org.lkvkn.gistrade.users.model.User;
import org.lkvkn.gistrade.users.service.entity.UserEntityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
public class UserGrpcServiceTest {

    public class UserGrpcObserver implements StreamObserver<UserGrpcDto> {
        private UserGrpcDto sent;
        private Throwable throwable;

        @Override
        public void onNext(UserGrpcDto value) {
            sent = value;            
        }

        @Override
        public void onError(Throwable throwable) {
            this.throwable = throwable;
        }

        public UserGrpcDto getSent() {
            return sent;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public void onCompleted() {}
    }

    public class UserGrpcCollectionObserver implements StreamObserver<AllUsersResponse> {
        private AllUsersResponse response;
        private Throwable throwable;

        public AllUsersResponse getResponse() {
            return response;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public void onNext(AllUsersResponse value) {
            response = value;
        }

        @Override
        public void onError(Throwable t) {
            throwable = t;
        }

        @Override
        public void onCompleted() {}
    }

    public class EmptyObserver implements StreamObserver<Empty> {
        private Empty response;
        private Throwable throwable;

        public Empty getResponse() {
            return response;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public void onNext(Empty value) {
            response = value;
        }

        @Override
        public void onError(Throwable t) {
            throwable = t;
        }

        @Override
        public void onCompleted() {}
    }

    @Mock
    private UserEntityService entityService;

    @Mock
    private DtoToEntityMapper dtoToEntityMapper;

    @Mock
    private EntityToDtoMapper entityToDtoMapper;

    private UserGrpcDto dto;
    private UserGrpcObserver responseObserver;

    @InjectMocks
    private UserGrpcService service;

    @BeforeEach
    public void beforeEach() {
        dto = UserGrpcDto.newBuilder().build();
        responseObserver = new UserGrpcObserver();
    }

    @Test
    public void onSuccessfulCreateShouldReturnTheSameObject() {
        // Arrange
        dto = UserGrpcDto.getDefaultInstance();
        User entityRequest = User.builder().build();
        when(dtoToEntityMapper.apply(dto)).thenReturn(entityRequest);
        when(entityToDtoMapper.apply(entityRequest)).thenReturn(dto);
        when(entityService.create(entityRequest)).thenReturn(entityRequest);

        // Act
        service.create(dto, responseObserver);

        // Assert
        assertEquals(dto, responseObserver.getSent());
        verify(entityService).create(entityRequest);
        verify(dtoToEntityMapper).apply(dto);
        verify(entityToDtoMapper).apply(entityRequest);
    }

    @Test
    public void onReadAllReturnCorrectCollection() {
        // Arrange
        User entity = new User();
        List<User> expectedEntity = List.of(entity);
        UserGrpcDto response = UserGrpcDto.newBuilder().build();
        when(entityService.readAll()).thenReturn(expectedEntity);
        when(entityToDtoMapper.apply(entity)).thenReturn(response);
        var observer = new UserGrpcCollectionObserver();

        // Act
        service.readAll(Empty.getDefaultInstance(), observer);

        // Assert
        assertEquals(response, observer.getResponse().getUsersList().getFirst());
        assertNotNull(observer.getResponse());
        verify(entityService).readAll();
    }

    @Test
    public void onReadAllShouldHandleEmptyList() {
        // Arrange
        when(entityService.readAll()).thenReturn(List.of());
        var observer = new UserGrpcCollectionObserver();

        // Act
        service.readAll(Empty.getDefaultInstance(), observer);

        // Assert
        assertTrue(observer.getResponse().getUsersList().isEmpty());
    }

    @Test
    public void onReadByIdEqualsNullShouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> service.readById(null, responseObserver));
    }

    @Test
    public void onReadByExistsIdShouldReturnEqualentObject() {
        // Arrange
        Long requestId = 1L;
        User expected = User.builder().id(requestId).build();
        UserGrpcDto responseExpected = UserGrpcDto.newBuilder()
                .setId(requestId)
                .build();
        FindUserByIdRequest request = FindUserByIdRequest.newBuilder()
                .setId(requestId)
                .build();
        when(entityService.read(requestId)).thenReturn(expected);
        when(entityToDtoMapper.apply(expected)).thenReturn(responseExpected);

        // Act
        service.readById(request, responseObserver);

        // Assert
        UserGrpcDto actual = responseObserver.getSent();
        assertEquals(responseExpected, actual);
        verify(entityService).read(requestId);
    }

    @Test
    public void whenPropsNotExistsShouldReturnEmptyObject() {
        // Arrange
        Map<String, String> emptyMap = new HashMap<>();
        List<User> expectedEntityList = List.of();
        FindUserByPropsRequest request = FindUserByPropsRequest.newBuilder()
                .putAllStringFields(emptyMap)
                .build();
        when(entityService.readByProps(emptyMap)).thenReturn(expectedEntityList);
        UserGrpcCollectionObserver observer = new UserGrpcCollectionObserver();

        // Act
        service.readByProps(request, observer);
        List<UserGrpcDto> actual = observer.getResponse().getUsersList();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void whenPropsExistShouldReturnListOfUsers() {
        // Arrange
        Map<String, String> props = new HashMap<>();
        props.put("firstName", "John");
        
        User user1 = User.builder().id(1L).firstName("John").build();
        User user2 = User.builder().id(2L).firstName("John").build();
        List<User> expectedEntities = List.of(user1, user2);
        
        UserGrpcDto dto1 = UserGrpcDto.newBuilder().setId(1L).setFirstName("John").build();
        UserGrpcDto dto2 = UserGrpcDto.newBuilder().setId(2L).setFirstName("John").build();
        
        FindUserByPropsRequest request = FindUserByPropsRequest.newBuilder()
                .putAllStringFields(props)
                .build();
        
        when(entityService.readByProps(props)).thenReturn(expectedEntities);
        when(entityToDtoMapper.apply(user1)).thenReturn(dto1);
        when(entityToDtoMapper.apply(user2)).thenReturn(dto2);
        
        UserGrpcCollectionObserver observer = new UserGrpcCollectionObserver();

        // Act
        service.readByProps(request, observer);
        
        // Assert
        assertEquals(2, observer.getResponse().getUsersList().size());
        assertEquals(dto1, observer.getResponse().getUsersList().get(0));
        assertEquals(dto2, observer.getResponse().getUsersList().get(1));
    }

    @Test
    public void onSuccessfulUpdateShouldReturnUpdatedObject() {
        // Arrange
        UserGrpcDto updateDto = UserGrpcDto.newBuilder()
                .setId(1L)
                .setFirstName("Updated")
                .build();
        User entityRequest = User.builder().id(1L).build();
        User updatedEntity = User.builder().id(1L).firstName("Updated").build();
        UserGrpcDto updatedDto = UserGrpcDto.newBuilder()
                .setId(1L)
                .setFirstName("Updated")
                .build();
        
        when(dtoToEntityMapper.apply(updateDto)).thenReturn(entityRequest);
        when(entityService.updateFully(entityRequest)).thenReturn(updatedEntity);
        when(entityToDtoMapper.apply(updatedEntity)).thenReturn(updatedDto);

        // Act
        service.update(updateDto, responseObserver);

        // Assert
        assertEquals(updatedDto, responseObserver.getSent());
    }

    @Test
    public void onPartialUpdateShouldReturnUpdatedObject() {
        // Arrange
        Long userId = 1L;
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "PartiallyUpdated");
        
        PartialUpdateRequest request = PartialUpdateRequest.newBuilder()
                .setUserId(userId)
                .putAllStringFields(updates)
                .build();
        
        User updatedEntity = User.builder().id(userId).firstName("PartiallyUpdated").build();
        UserGrpcDto updatedDto = UserGrpcDto.newBuilder()
                .setId(userId)
                .setFirstName("PartiallyUpdated")
                .build();
        
        when(entityService.partialUpdate(userId, updates)).thenReturn(updatedEntity);
        when(entityToDtoMapper.apply(updatedEntity)).thenReturn(updatedDto);

        // Act
        service.partialUpdate(request, responseObserver);

        // Assert
        assertEquals(updatedDto, responseObserver.getSent());
        verify(entityService).partialUpdate(userId, updates);
    }

    @Test
    public void onPartialUpdateWithNullRequestShouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> service.partialUpdate(null, responseObserver));
    }

    @Test
    public void onDeleteShouldRemoveUserSuccessfully() {
        // Arrange
        Long userId = 1L;
        FindUserByIdRequest request = FindUserByIdRequest.newBuilder()
                .setId(userId)
                .build();
        EmptyObserver observer = new EmptyObserver();

        // Act
        service.delete(request, observer);

        // Assert
        verify(entityService).delete(userId);
        assertNotNull(observer.getResponse());
    }

    @Test
    public void onDeleteWithNullRequestShouldThrowException() {
        EmptyObserver observer = new EmptyObserver();
        assertThrows(NullPointerException.class,
                () -> service.delete(null, observer));
    }

    @Test
    public void onReadByIdWithNullIdInRequestShouldStillCallService() {
        // Arrange
        FindUserByIdRequest request = FindUserByIdRequest.newBuilder().build();
        when(entityService.read(0L)).thenThrow(new UserNotFoundException("Not found"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, 
                () -> service.readById(request, responseObserver));
    }
}