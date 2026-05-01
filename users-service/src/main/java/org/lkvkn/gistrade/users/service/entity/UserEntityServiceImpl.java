package org.lkvkn.gistrade.users.service.entity;

import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.users.exceptions.UserNotFoundException;
import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.users.exceptions.UserAlreadyExistsException;
import org.lkvkn.gistrade.users.model.User;
import org.lkvkn.gistrade.users.repository.UserQueryRepository;
import org.lkvkn.gistrade.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private UserRepository repository;
    private UserQueryRepository queryRepository;

    @Override
    public User create(User user) throws UserAlreadyExistsException {
        String username = user.getUsername();
        if (repository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }
        user.setId(null);
        return repository.save(user);
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public User read(Long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> readAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public User updateFully(User dto) throws UserAlreadyExistsException, UserNotFoundException {
        log.debug("Fully updating user with id: {}", dto.getId());
        Long userId = dto.getId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        User existing = repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        if (!existing.getUsername().equals(dto.getUsername())) {
            if (repository.existsByUsername(dto.getUsername())) {
                throw new UserAlreadyExistsException(dto.getUsername());
            }
        }
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPatronimyc(dto.getPatronimyc());
        existing.setUsername(dto.getUsername());
        existing.setPassword(dto.getPassword());
        existing.setRole(dto.getRole());
        return repository.save(existing);
    }

    @Override
    public User readByUsername(String username) throws UserNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    @Transactional
    public User partialUpdate(Long userId, Map<String, String> stringFieldsMap) {
        User found = read(userId);
        for (var entry : stringFieldsMap.entrySet()) {
            String newValue = entry.getValue();
            switch (entry.getKey()) {
                case "username" -> changeUsername(found, newValue);
                case "first_name" -> found.setFirstName(newValue);
                case "last_name" -> found.setLastName(newValue);
                case "patronimyc" -> found.setPatronimyc(newValue);
                case "password" -> found.setPassword(newValue);
                case "role" -> found.setRole(AppRole.valueOf(newValue.toUpperCase()));
            }
        }
        return repository.save(found);
    }

    private void changeUsername(User found, String username) {
        if (!found.getUsername().equals(username) && repository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }
        found.setUsername(username);
    }

    @Override
    public List<User> readByProps(Map<String, String> properties) {
        if (properties == null) {
            throw new NullPointerException();
        }
        return queryRepository.findByProperties(properties);
    }
}
