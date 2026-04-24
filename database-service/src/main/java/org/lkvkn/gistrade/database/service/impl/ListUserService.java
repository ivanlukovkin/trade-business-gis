package org.lkvkn.gistrade.database.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.lkvkn.gistrade.database.model.User;
import org.lkvkn.gistrade.database.service.UserCrudService;
import org.springframework.stereotype.Service;

@Service
public class ListUserService implements UserCrudService {

    private Long idCounter = 1L;
    private final List<User> users = new ArrayList<>();

    @Override
    public User create(User sample) {
        sample.setId(idCounter++);    
        users.add(sample);
        return sample;
    }

    @Override
    public User read(Long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }
    @Override
    public List<User> readAll() {
        return users;
    }
    @Override
    public User update(User sample) {
        User found = readUserWithNullCheck(sample.getId());
        found.setFullName(sample.getFullName());
        found.setPassword(sample.getPassword());
        found.setRole(sample.getRole());
        found.setUsername(sample.getUsername());
        return found;

    }
    @Override
    public void delete(Long id) {
        readUserWithNullCheck(id);
        users.removeIf(user -> user.getId() == id);
    }

    private User readUserWithNullCheck(Long id) {
        User found = read(id);
        if (found == null) {
            var message = String.format("User with id: '%s' not found.", id);
            throw new RuntimeException(message);
        }
        return found;
    }
}
