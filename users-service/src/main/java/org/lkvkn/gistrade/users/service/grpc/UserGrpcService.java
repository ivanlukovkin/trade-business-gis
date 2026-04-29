package org.lkvkn.gistrade.users.service.grpc;

import java.util.function.Function;

import org.lkvkn.gistrade.service.users.AllUsersResponse;
import org.lkvkn.gistrade.service.users.FindUserByIdRequest;
import org.lkvkn.gistrade.service.users.FindUserByPropsRequest;
import org.lkvkn.gistrade.service.users.PartialUpdateRequest;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.lkvkn.gistrade.service.users.UserServiceGrpc;
import org.lkvkn.gistrade.users.mapper.DtoToEntityMapper;
import org.lkvkn.gistrade.users.mapper.EntityToDtoMapper;
import org.lkvkn.gistrade.users.model.User;
import org.lkvkn.gistrade.users.service.entity.UserEntityService;
import org.lognet.springboot.grpc.GRpcService;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;

@GRpcService
@AllArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private UserEntityService entityService;
    private DtoToEntityMapper dtoToEntityMapper;
    private EntityToDtoMapper entityToDtoMapper;

    @Override
    public void create(UserGrpcDto request, StreamObserver<UserGrpcDto> responseObserver) {
        applyWithDto(request, responseObserver, entityService::create);
    }

    @Override
    public void readAll(Empty request, StreamObserver<AllUsersResponse> responseObserver) {
        responseObserver.onNext(AllUsersResponse.newBuilder()
                .addAllUsers(entityService.readAll()
                        .stream()
                        .map(entityToDtoMapper::apply)
                        .toList())
                .build());  
        responseObserver.onCompleted();
    }

    @Override
    public void readById(FindUserByIdRequest request, StreamObserver<UserGrpcDto> responseObserver) {
        sendEntity(entityService.read(request.getId()), responseObserver);
    }

    @Override
    public void readByProps(FindUserByPropsRequest request, 
            StreamObserver<AllUsersResponse> responseObserver) {
        responseObserver.onNext(AllUsersResponse.newBuilder()
                .addAllUsers(entityService
                        .readByProps(request.getStringFieldsMap())
                        .stream()
                        .map(entityToDtoMapper::apply)
                        .toList())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void update(UserGrpcDto request, StreamObserver<UserGrpcDto> responseObserver) {
        applyWithDto(request, responseObserver, entityService::updateFully);
    }

    @Override
    public void partialUpdate(PartialUpdateRequest request,
            StreamObserver<UserGrpcDto> responseObserver) {
        sendEntity(entityService.partialUpdate(request.getUserId(), request.getStringFieldsMap()), 
                responseObserver);
    }

	@Override
	public void delete(FindUserByIdRequest request, StreamObserver<Empty> responseObserver) {
        entityService.delete(request.getId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
	}

    private void applyWithDto(UserGrpcDto request, StreamObserver<UserGrpcDto> responseObserver,
        Function<User, User> action) {
        sendEntity(action.apply(dtoToEntityMapper.apply(request)), responseObserver);
    }

    
	private void sendEntity(User entityResponse, StreamObserver<UserGrpcDto> responseObserver) {
        responseObserver.onNext(entityToDtoMapper.apply(entityResponse));
        responseObserver.onCompleted();
	}
}
