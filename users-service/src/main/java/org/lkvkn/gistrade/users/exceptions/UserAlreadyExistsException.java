package org.lkvkn.gistrade.users.exceptions;

import jakarta.persistence.EntityExistsException;

public class UserAlreadyExistsException extends EntityExistsException {

    public UserAlreadyExistsException(String username) {
        super("User with username = %s are already exists.".formatted(username));
    }
    
}
