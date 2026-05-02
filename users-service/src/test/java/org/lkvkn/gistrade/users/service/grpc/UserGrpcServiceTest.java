package org.lkvkn.gistrade.users.service.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lkvkn.gistrade.service.users.AllUsersResponse;
import org.lkvkn.gistrade.service.users.FindUserByIdRequest;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
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

        public Throwable geThrowable() {
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
        User excepted = User.builder().id(requestId).build();
        UserGrpcDto responseExcepted = UserGrpcDto.newBuilder()
                .setId(requestId)
                .build();
        FindUserByIdRequest request = FindUserByIdRequest.newBuilder()
                .setId(requestId)
                .build();
        when(entityService.read(requestId)).thenReturn(excepted);
        when(entityToDtoMapper.apply(excepted)).thenReturn(responseExcepted);

        // Act
        service.readById(request, responseObserver);

        // Assert
        UserGrpcDto actual = responseObserver.getSent();
        assertEquals(responseExcepted, actual);
    }
}
