package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.util.List;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface BillService {
    /**
     * Добавление нового счета
     *
     * @param id данные пользователя
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addBill(long id);

    /**
     * Запрос к репозиторию для получения счёта по параметру
     *
     * @param id данные пользователя
     * @return возвращает счет
     * @throws BillNotFoundException если счёта нет
     */
    Bill getBillById(long id) throws BillNotFoundException;

    /**
     * Запрос к репозиторию для получения счёта по параметру
     *
     * @param id данные пользователя
     * @param login данные пользователя
     * @return возвращает счет
     * @throws NoAccessException если нет доступа
     * @throws BillNotFoundException если нет счёта
     * @throws UserNotFoundException если нет пользователя
     */
    Bill getBillByIdAndLogin(long id, String login)
            throws NoAccessException, BillNotFoundException, UserNotFoundException;

    /**
     * Запрос к репозиторию для получения списка счетов пользователя
     *
     * @param id данные пользователя
     * @return возращает список счетов пользователя
     */
    List<Bill> getAllBillsByUser(long id);

    /**
     * Запрос к репозиторию для поплнеия баланса счёта
     *
     * @param billId данные счёта
     * @param sum сумма пополнения
     * @return возвращает true - при пополнении, false - при ошибке
     */
    boolean plusBalance(long billId, double sum);

    /**
     * Запрос к репозиторию для уменьшения баланрса
     *
     * @param billId данные счёта
     * @param sum сумма пополнения
     * @return возвращает true - при пополнении, false - при ошибке
     */
    boolean minusBalance(long billId, double sum);

    /**
     * Запрос к репозиторию для получения баланса счёта
     *
     * @param billId данные счёта
     * @param login данные пользователя
     * @return возвращает баланс счёта
     * @throws NoAccessException если нет доступа
     * @throws BillNotFoundException если нет счёта
     * @throws UserNotFoundException если нет пользователя
     */
    double getBalance(long billId, String login) throws NoAccessException, BillNotFoundException, UserNotFoundException;
}
