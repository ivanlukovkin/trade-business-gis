package org.lkvkn.gistrade.users.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String username) {
        super("User with username = %s not found.".formatted(username));
    }

    public UserNotFoundException(Long id) {
        super("User with id = %d not found.".formatted(id));
    }

}
