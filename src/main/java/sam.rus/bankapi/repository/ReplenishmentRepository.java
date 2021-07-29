package sam.rus.bankapi.repository;

import sam.rus.bankapi.entity.Replenishment;

import java.util.List;

/**
 * Слой для запросов к базе данных пополнеия счёта
 */
public interface ReplenishmentRepository {
    /**
     * Запрос к базе данных для создания пополнения счёта
     *
     * @param replenishment данные пополнения
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addReplenishment(Replenishment replenishment);

    /**
     * Запрос к базе данных для получения списка всех пополнений для счёта
     *
     * @param billId параметры счёта
     * @return возвращает список всех пополнений для счёта
     */
    List<Replenishment> getAllReplenishmentByBill(long billId);
}
