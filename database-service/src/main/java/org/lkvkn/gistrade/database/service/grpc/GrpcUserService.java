package org.lkvkn.gistrade.database.service.grpc;

import org.lkvkn.gistrade.service.entity.FindUserRequest;
import org.lkvkn.gistrade.service.entity.UserGrpcDto;
import org.lkvkn.gistrade.service.entity.UserGrpcDtos;
import org.lkvkn.gistrade.service.entity.UserServiceGrpc;
import org.lkvkn.gistrade.database.mapper.user.UserEntityToGrpcDtoMapper;
import org.lkvkn.gistrade.database.mapper.user.UserGrpcDtoToEntityMapper;
import org.lkvkn.gistrade.database.service.UserCrudService;
import org.lognet.springboot.grpc.GRpcService;

import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GRpcService
@AllArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private UserCrudService userService;
    private UserEntityToGrpcDtoMapper entityToGrpcDtoMapper;
    private UserGrpcDtoToEntityMapper grpcDtoToEntityMapper;

    @Override
    public void create(UserGrpcDto request, StreamObserver<UserGrpcDto> requestObserver) {
        requestObserver.onNext(entityToGrpcDtoMapper.apply(
                userService.create(grpcDtoToEntityMapper.apply(request))));
        requestObserver.onCompleted();
    }

    @Override
    public void readAll(Empty request, StreamObserver<UserGrpcDtos> requestObserver) {
        requestObserver.onNext(UserGrpcDtos.newBuilder()
                .addAllUsers(userService.readAll()
                        .stream()
                        .map(entityToGrpcDtoMapper::apply)
                        .toList())
                .build());      
        requestObserver.onCompleted();
    }

    @Override
    public void read(FindUserRequest request, StreamObserver<UserGrpcDto> requestObserver) {
        try {
            requestObserver.onNext(entityToGrpcDtoMapper.apply(
                    userService.read(request.getId())));
            requestObserver.onCompleted();
        } catch (RuntimeException exception) {
            requestObserver.onError(Status.NOT_FOUND
                    .withDescription(exception.getMessage())
                    .asRuntimeException());
        } catch (Exception exception) {
            log.error(exception.toString());
            requestObserver.onError(exception);
        }
    }

    @Override
    public void update(UserGrpcDto request, StreamObserver<UserGrpcDto> requestObserver) {
        try {
            requestObserver.onNext(entityToGrpcDtoMapper.apply(
                    userService.update(grpcDtoToEntityMapper.apply(request)))); 
            requestObserver.onCompleted();
        } catch (Exception exception) {
            requestObserver.onError(exception);
        }
    }

    @Override
    public void delete(FindUserRequest request, StreamObserver<Empty> requestObserver) {
        try {
            userService.delete(request.getId());
            requestObserver.onNext(Empty.getDefaultInstance());
            requestObserver.onCompleted();
        } catch (Exception exception) {
            requestObserver.onError(exception);
        }
    }
} 
