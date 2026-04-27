package org.lkvkn.gistrade.common.service;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

/**
 * Обобщенный интерфейс для реализации CRUD-операций над сущностями базы данных.
 */
public interface CrudEntityService<Entity, PrimaryKey extends Serializable> {

    /**
     * Записывает новую сущность в базу данных
     * @param entity - сведения о записываемой сущности
     * @return Созданная сущность в базе данных
     * @throws EntityExistsException выбрасывает исключение при конфликте ункальных полей
     */
    Entity create(Entity entity) throws EntityExistsException;

    /**
     * Чтение сущности из базы данных
     * @param primaryKey - Первичный ключ сущности
     * @return Найденная сущность
     * @throws EntityNotFoundException выбрасывает исключение при отсутствии заданного значения
     */
    Entity read(PrimaryKey primaryKey) throws EntityNotFoundException;

    /**
     * Чтение всех существующих сущностей
     * @return список сущностей
     */
    List<Entity> readAll();

    /**
     * Полное обновление сущности
     * @param entity - Сущность, содержащая все поля для изменения
     * @return Обновленная сущность
     * @throws EntityExistsException При обновлении полей с признаком уникальности
     * выбрасывает исключение, если сущности с таким же полем уже существуют
     * @throws EntityNotFoundException При отсутствии сущности, которую необходимо изменить,
     * выбрасывает исключение о ненахождении
     */
    Entity updateFully(Entity entity) throws EntityExistsException, EntityNotFoundException;

    /**
     * Удаление сущности
     * @param primaryKey Первичный ключ для поиска
     * @throws EntityNotFoundException Исключение при безуспешном поиске удаляемой сущности
     */
    void delete(PrimaryKey primaryKey) throws EntityNotFoundException;
}
