package org.lkvkn.gistrade.users.service.entity;

import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.common.service.CrudEntityService;
import org.lkvkn.gistrade.users.exceptions.UserNotFoundException;
import org.lkvkn.gistrade.users.model.User;

/**
 * Сервис работы с пользователем
 */
public interface UserEntityService extends CrudEntityService<User, Long> {

    /**
     * Поиск пользователя по логину
     * @param username логин
     * @return найденная сущность
     * @throws UserNotFoundException Выбрасывает исключение при ненахождении пользователя
     */
    User readByUsername(String username) throws UserNotFoundException;

    /**
     * Поиск пользователей по набору свойств 
     * @param stringFieldsMap - соответствие имени поля к значению
     * @return
     */
    List<User> readByProps(Map<String, String> stringFieldsMap);

    /**
     * Частичное обновление сущности
     * @param userId - Идентификатор пользователя
     * @param stringFieldsMap - соответствие имени поля к значение
     * @return
     */
    User partialUpdate(Long userId, Map<String, String> stringFieldsMap);
}
