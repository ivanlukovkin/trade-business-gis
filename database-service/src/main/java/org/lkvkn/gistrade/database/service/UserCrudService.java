package org.lkvkn.gistrade.database.service;

import java.util.List;

import org.lkvkn.gistrade.database.model.User;

public interface UserCrudService {
    User create(User sample);
    User read(Long id);
    List<User> readAll();
    User update(User sample);
    void delete(Long id);
}
