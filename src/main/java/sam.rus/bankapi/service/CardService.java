package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.Card;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.CardNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.util.List;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface CardService {
    /**
     * Запрос к репозиторию для создания новой карты
     *
     * @param login данные пользователя
     * @param billId номер счёта
     * @return возвращает true - при создании, false - при ошибке
     * @throws UserNotFoundException выбрасывает исключение, если пользователя нет
     */
    boolean addCard(String login, long billId) throws UserNotFoundException;

    /**
     * Запрос к репозиторию для получения карты по параметру
     *
     * @param id данные карты
     * @param login данные пользователя
     * @return возвращает карту
     * @throws BillNotFoundException если нет счета
     * @throws CardNotFoundException если нет карты
     * @throws NoAccessException если нет доступа
     * @throws UserNotFoundException если нет пользователя
     */
    Card getCardById(long id, String login)
            throws BillNotFoundException, CardNotFoundException, NoAccessException, UserNotFoundException;

    /**
     * Запрос к репозиторию для получения списка карт по счёту
     *
     * @param id данные счёта
     * @param login данные пользователя
     * @return вовзращает список карт
     * @throws NoAccessException если нет доступа
     * @throws BillNotFoundException если нет счета
     * @throws UserNotFoundException если нет пользователя
     */
    List<Card> getAllCardsByBill(long id, String login)
            throws NoAccessException, BillNotFoundException, UserNotFoundException;

    /**
     * Запрос к репозиторию для получения списка всех карт
     *
     * @return возвращает список карт
     */
    List<Card> getAllCards();

    /**
     * Запрос к репозиторию для получения списка карт с необходимым статусом
     *
     * @param status необходимый статус
     * @return возвращает список карт с необходимым статусом
     */
    List<Card> getAllCardsByStatus(String status);

    /**
     * Запрос к базе данных для смены статуса карты
     *
     * @param id данные карты
     * @param status необходимый статус
     * @return возвращает true - при смене, false - при ошибке
     */
    boolean changeCardStatus(long id, String status);
}
