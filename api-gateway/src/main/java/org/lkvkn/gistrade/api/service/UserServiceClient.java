package org.lkvkn.gistrade.api.service;

import org.lkvkn.gistrade.service.users.FindUserByIdRequest;
import org.lkvkn.gistrade.service.users.FindUserByPropsRequest;
import org.lkvkn.gistrade.service.users.PartialUpdateRequest;
import org.lkvkn.gistrade.service.users.UserGrpcDto;
import org.lkvkn.gistrade.service.users.UserServiceGrpc;
import org.lkvkn.gistrade.service.users.UserServiceGrpc.UserServiceBlockingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.api.mapper.UsersGrpcDtoMapper;
import org.lkvkn.gistrade.api.mapper.UsersWebDtoMapper;
import org.lkvkn.gistrade.api.model.UserWebDto;

@Slf4j
@Service
public class UserServiceClient {

    private ManagedChannel channel;
    private UserServiceBlockingStub serviceStub;
    private UsersWebDtoMapper webDtoMapper;
    private UsersGrpcDtoMapper grpcDtoMapper;

    public UserServiceClient(
            @Value("${org.lkvkn.gistrade.service.users.host}") String host,
            @Value("${org.lkvkn.gistrade.service.users.port}") int port,
            UsersWebDtoMapper webDtoMapper,
            UsersGrpcDtoMapper grpcDtoMapper
        ) {
        channel = ManagedChannelBuilder
                .forTarget("%s:%d".formatted(host, port))
                .usePlaintext()
                .build();
        serviceStub = UserServiceGrpc.newBlockingStub(channel);
        this.webDtoMapper = webDtoMapper;
        this.grpcDtoMapper = grpcDtoMapper;
    }

    public UserWebDto create(UserWebDto request) {
        return webDtoMapper.apply(serviceStub.create(grpcDtoMapper.apply(request)));
    }

    public List<UserWebDto> readAll() {
        return serviceStub.readAll(Empty.getDefaultInstance())
            .getUsersList()
            .stream()
            .map(webDtoMapper::apply)
            .toList();
    }

    public UserWebDto readById(Long id) {
        return webDtoMapper.apply(serviceStub
                .readById(FindUserByIdRequest
                        .newBuilder()
                        .setId(id)
                        .build()));
    }

    public List<UserWebDto> readByProps(Map<String, String> properties) {
        return serviceStub.readByProps(FindUserByPropsRequest
                .newBuilder()
                .putAllStringFields(properties)
                .build())
                .getUsersList()
                .stream()
                .map(webDtoMapper)
                .toList();
    }

    public UserWebDto update(Long id, UserWebDto request) {
        request.setId(id);
        System.out.println(request.toString());
        UserGrpcDto dto = serviceStub.update(grpcDtoMapper.apply(request));
        return webDtoMapper.apply(dto);
    }
    
    public UserWebDto partialUpdate(Long id, Map<String, String> request) {
        return webDtoMapper.apply(
                serviceStub.partialUpdate(PartialUpdateRequest
                        .newBuilder()
                        .setUserId(id)
                        .putAllStringFields(request)
                        .build()));
    }

    public void delete(Long id) {
        serviceStub.delete(FindUserByIdRequest
                .newBuilder()
                .setId(id)
                .build());
    }

}
