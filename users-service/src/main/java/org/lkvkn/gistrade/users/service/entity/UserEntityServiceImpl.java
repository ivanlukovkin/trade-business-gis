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

import lombok.AllArgsConstructor;

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
    public User updateFully(User dto) throws UserAlreadyExistsException, UserNotFoundException {
        Long userId = dto.getId();
        if (!repository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return repository.save(dto);
    }

    @Override
    public User readByUsername(String username) throws UserNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User updateFullName(Long primaryKey, String firstName, String lastName, String patronimyc)
            throws UserNotFoundException {
        User found = read(primaryKey);
        found.setFirstName(firstName);
        found.setLastName(lastName);
        found.setPatronimyc(patronimyc);
        return repository.save(found);
    }

    @Override
    public User updateUsername(Long primaryKey, String username)
            throws UserNotFoundException, UserAlreadyExistsException {
        User found = read(primaryKey);
        if (found.getUsername() != username && repository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }
        found.setUsername(username);
        return repository.save(found);
    }

    @Override
    public User updatePassword(Long primaryKey, String password) throws UserNotFoundException {
        User found = read(primaryKey);
        found.setPassword(password);
        return repository.save(found);
    }

	@Override
	public User updateRole(Long primaryKey, AppRole role) throws UserNotFoundException {
        if (role == null) {
            throw new RuntimeException("Role cannot be null.");
        }
        User found = read(primaryKey);
        found.setRole(role);
        return repository.save(found);
	}

	@Override
	public User updateRole(Long primaryKey, String roleName) throws UserNotFoundException {
        return updateRole(primaryKey, AppRole.valueOf(roleName));
	}

    @Override
    public User partialUpdate(long userId, Map<String, String> stringFieldsMap) {
        return partialUpdate(userId, stringFieldsMap);
    }

    @Override
    public List<User> readByProps(Map<String, String> properties) {
        return queryRepository.findByProperties(properties);
    }

}
