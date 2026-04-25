---
title: Сущности базы данных
output: pdf_document
---

# Сущности базы данных

## Пользователь

__Наименование таблицы__: `app_user`

| Имя атрибута | Тип данных | Ограничения | Описание |
| ------------ | ---------- | ----------- | -------- |
| `id` | `BIGSERIAL` | `PRIMARY KEY`, `NOT NULL` | Идентификатор пользователя |
| `username` | `VARCHAR(128)` | `UNIQUE`, `NOT NULL` | Логин пользователя |
| `auth_password` | `VARCHAR(255)` | `NOT NULL` | Пароль для авторизации |
| `full_name` | `VARCHAR(255)` | `NOT NULL` | ФИО пользователя |

```sql
CREATE TABLE app_user
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(128) UNIQUE NOT NULL,
    auth_password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL
);
```
