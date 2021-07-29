package sam.rus.bankapi.repository;

import sam.rus.bankapi.entity.Bill;

import java.util.List;
import java.util.Optional;

/**
 * Слой для запросов к базе данных счетов
 */
public interface BillRepository {

    /**
     * Запрос к базе данных для создания нового счёта для пользователя
     *
     * @param userId данные пользователя
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addBill(long userId);

    /**
     * Запрос к базе данных для получения счёта по параметру
     *
     * @param billId данные счёта
     * @return возращает контейнер объекта, который может содержать null, при его отсутсвии
     */
    Optional<Bill> getBillById(long billId);

    /**
     * Запрос к базе данных для получения списка счетов пользователя
     *
     * @param userId данные пользователя
     * @return возращает список счетов пользователя
     */
    List<Bill> getAllBillsByUser(long userId);

    /**
     * Запрос к базе данных для получения баланса счёта
     *
     * @param billId данные счёта
     * @return возвращает баланс счёта
     */
    double getBalanceBill(long billId);

    /**
     * Запрос к базе данных для пополнения баланса счёта
     *
     * @param billId данные счёта
     * @param sum сумма пополнеия
     * @return возвращает true - при пополнении, false - при ошибке
     */
    boolean plusBalance(long billId, double sum);

    /**
     * Запрос к базе данных для уменьшение баланса счёта
     *
     * @param billId данные счёта
     * @param sum сумма уменьшения
     * @return возвращает true - при уменьшение, false - при ошибке
     */
    boolean minusBalance(long billId, double sum);
}
