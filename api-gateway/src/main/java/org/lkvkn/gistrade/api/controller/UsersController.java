package org.lkvkn.gistrade.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.api.model.UserWebDto;
import org.lkvkn.gistrade.api.service.UserServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@AllArgsConstructor
@RequestMapping("users")
public class UsersController {

    private UserServiceClient serviceClient;

    @PostMapping
    public ResponseEntity<UserWebDto> create(@RequestBody UserWebDto request) {
        return ResponseEntity.ok(serviceClient.create(request));
    }

    @GetMapping
    public ResponseEntity<List<UserWebDto>> readAll() {
        return ResponseEntity.ok(serviceClient.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWebDto> readById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceClient.readById(id));
    }

    @GetMapping("/by-props")
    public ResponseEntity<List<UserWebDto>> readByProps(
            @RequestBody Map<String, String> properties) {
        return ResponseEntity.ok(serviceClient.readByProps(properties));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserWebDto> update(
        @PathVariable Long id,
        @RequestBody UserWebDto request) {
        return ResponseEntity.ok(serviceClient.update(id, request));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserWebDto> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, String> properties) {
        return ResponseEntity.ok(serviceClient.partialUpdate(id, properties));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceClient.delete(id);
        return ResponseEntity.ok().build();
    }

}
