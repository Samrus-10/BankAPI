package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.util.List;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface ReplenishmentService {
    /**
     * Запрос к репозиторию для создания пополнения счёта
     *
     * @param replenishment данные пополнения
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addReplenishment(Replenishment replenishment);

    /**
     * Запрос к репозиторию для получения списка всех пополнений для счёта
     *
     * @param id данные счёта
     * @param login данные пользователя
     * @return возвращает список всех пополнений для счёта
     * @throws BillNotFoundException если нет счета
     * @throws NoAccessException если нет доступа
     * @throws UserNotFoundException если нет пользователя
     */
    List<Replenishment> getAllReplenishmentByBill(long id, String login)
            throws BillNotFoundException, NoAccessException, UserNotFoundException;
}
