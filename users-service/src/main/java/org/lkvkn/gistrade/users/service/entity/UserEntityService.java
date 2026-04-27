package org.lkvkn.gistrade.users.service.entity;

import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.common.service.CrudEntityService;
import org.lkvkn.gistrade.users.exceptions.UserAlreadyExistsException;
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
     * Обновляет ФИО пользователя
     * @param primaryKey Первичный ключ записи
     * @param fullName Записываемое ФИО пользователя
     * @return Обновленная сущность
     * @throws UserNotFoundException Выбрасывает исключение при ненахождении пользователя
     */
    User updateFullName(Long primaryKey, String firstName, String lastName, String patronimyc)
            throws UserNotFoundException;

    /**
     * Обновляет логин пользователя
     * @param primaryKey Первичный ключ записи
     * @param username Записываемый логин пользователя
     * @return Обновленная сущность
     * @throws UserAlreadyExistsException Выбрасывает исключение при ненахождении пользователя
     */
    User updateUsername(Long primaryKey, String username) 
            throws UserNotFoundException, UserAlreadyExistsException;

    /**
     * Обновляет пароль пользователя
     * @param primaryKey Первичный ключ записи
     * @param password Записываемый пароль
     * @return Обновленная сущность
     * @throws UserNotFoundException Выбрасывает исключение при ненахождении пользователя
     */
    User updatePassword(Long primaryKey, String password) throws UserNotFoundException;

    /**
     * Обновляет роль пользователя
     * @param primaryKey Первичный ключ
     * @param role Новая роль
     * @return Обновленная сущность
     * @throws UserNotFoundException Выбрасывает исключение при ненахождении пользователя
     */
    User updateRole(Long primaryKey, AppRole role) throws UserNotFoundException;

    /**
     * Обновляет роль пользователя
     * @param primaryKey Первичный ключ
     * @param role Новая роль (в строковом формате)
     * @return Обновленная сущность
     * @throws UserNotFoundException Выбрасывает исключение при ненахождении пользователя
     */
    User updateRole(Long primaryKey, String roleName) throws UserNotFoundException;

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
    User partialUpdate(long userId, Map<String, String> stringFieldsMap);
}
