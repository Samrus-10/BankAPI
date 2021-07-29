package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.UserNotFoundException;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface UserService {
    /**
     * Запрос к репозиторию для создания нового пользователя
     *
     * @param user данные пользователя
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addUser(User user);

    /**
     * Запрос к репозиторию для получения пользователя по параметру
     *
     * @param login данные пользователя
     * @return возвращает пользователя
     * @throws UserNotFoundException если пользователя нет
     */
    long getUserIdByLogin(String login) throws UserNotFoundException;

    /**
     * Запрос к репозиторию для аутентификации
     *
     * @param login данные пользователя
     * @param password данные пользователя
     * @return true - если аутентификация прошла, false - если аутентификации не прошла
     */
    boolean authentication(String login, String password);

    /**
     * Запрос к репозиторию для получения роли пользователя
     *
     * @param login данные пользователя
     * @return роль пользовтеля
     * @throws UserNotFoundException если пользователя нет
     */
    Role getRoleByLogin(String login) throws UserNotFoundException, UserNotFoundException;

    /**
     * Запрос к репозиторию для получения пользователя по логину
     *
     * @param login данные пользователя
     * @return возвращает пользователя
     * @throws UserNotFoundException если пользователя нет
     */
    User getUserByLogin(String login) throws UserNotFoundException;
}
