package sam.rus.bankapi.repository;

import sam.rus.bankapi.entity.Operation;

import java.util.List;
import java.util.Optional;

/**
 * Слой для запросов к базе данных операций
 */
public interface OperationRepository {

    /**
     * Запрос к базе данных для создания новой операции
     *
     * @param operation данные операции
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addOperation(Operation operation);

    /**
     * Запрос к базе данных для получения списка всех операций по счёту
     *
     * @param billId данные счёта
     * @return возвращает список операция по счёту
     */
    List<Operation> getAllOperationByBill(long billId);

    /**
     * Запрос к базе данных для получения списка всех операция
     *
     * @return возвращает список операций
     */
    List<Operation> getAllOperation();

    /**
     * Запрос к базе данных для получения списка всех операция с необходимым статусом
     *
     * @param status необходимый статус
     * @return возвращает список операций с необходимым статусом
     */
    List<Operation> getAllOperationsByStatus(String status);

    /**
     * Запрос к базе данных для смены статуса операции
     *
     * @param operationId данные операции
     * @param status необходимый статус
     * @return возвращает true - при смене, false - при ошибке
     */
    boolean changeOperationStatus(long operationId, String status);

    /**
     * Запрос к базе данных для получения операции по параметру
     *
     * @param operationId данные операции
     * @return возвращает контейнер объекта, который может содержать null, при его отсутсвии
     */
    Optional<Operation> getOperationById(long operationId);
}
