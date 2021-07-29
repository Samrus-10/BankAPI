package sam.rus.bankapi.repository;

import sam.rus.bankapi.entity.User;

import java.util.Optional;

/**
 * Слой для запросов к базе данных пользователей
 */
public interface UserRepository {

    /**
     * Запрос к базе данных для создания нового пользователя
     *
     * @param user данные пользователя
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addUser(User user);

    /**
     * Запрос к базе данных для получения пользователя по параметру
     *
     * @param login данные пользователя
     * @return возращает контейнер объекта, который может содержать null, при его отсутсвии
     */
    Optional<User> getUserByLogin(String login);
}
