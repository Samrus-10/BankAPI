package sam.rus.bankapi.repository;

import java.sql.Connection;

/**
 * Слой пулов соединения с БД
 */
public interface ConnectionPoll {
    /**
     * Запрос на получения connection
     *
     * @return возвращает connection
     */
    Connection getConnection();
    /**
     * Запрос на закрытие пула соединения
     *
     * @return возвращает булевое значения, которая говорить о успешности операции
     */
    boolean closeConnectionPool();
}
