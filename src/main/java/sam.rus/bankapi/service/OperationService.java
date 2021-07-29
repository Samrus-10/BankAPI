package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.OperationNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.util.List;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface OperationService {
    /**
     * Запрос к репозиторию для создания новой операции
     *
     * @param operation данные операции
     * @return возвращает true - при создании, false - при ошибке
     * @throws BillNotFoundException если нет счёта
     */
    boolean addOperation(Operation operation) throws BillNotFoundException;

    /**
     * Запрос к репозиторию для получения списка всех операций по счёту
     *
     * @param id данные счёта
     * @param login данные пользователя
     * @return возвращает список операция по счёту
     * @throws BillNotFoundException если нет счёта
     * @throws OperationNotFoundException если нет операции
     * @throws NoAccessException если нет доступа
     * @throws UserNotFoundException если нет пользователя
     */
    List<Operation> getAllOperationsByBillId(long id, String login)
            throws BillNotFoundException, OperationNotFoundException, NoAccessException, UserNotFoundException;

    /**
     * Запрос к репозиторию для получения списка всех операция
     *
     * @return возвращает список операций
     */
    List<Operation> getAllOperations();

    /**
     * Запрос к репозиторию для получения списка всех операция с необходимым статусом
     *
     * @param status необходимый статус
     * @return возвращает список операций с необходимым статусом
     */
    List<Operation> getAllOperationsByStatus(String status);

    /**
     * Запрос к репозиторию для поулчения операции по параметру
     *
     * @param id данные операции
     * @param status необходимый статус
     * @return возвращает true - при смене, false - при ошибке
     * @throws OperationNotFoundException если нет операции
     */
    boolean changeStatusOperation(long id, String status) throws OperationNotFoundException;
}
