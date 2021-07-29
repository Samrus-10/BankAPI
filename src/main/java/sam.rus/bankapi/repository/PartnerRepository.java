package sam.rus.bankapi.repository;

import sam.rus.bankapi.entity.Partner;

import java.util.List;

/**
 *  Слой для запросов к базе данных контрагентов
 */
public interface PartnerRepository {
    /**
     * Запрос к базе данных для создания нового контрагента
     *
     * @param partner данные контрагента
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addPartner(Partner partner);

    /**
     * Запрос к базе данных для получения списка всех контрагентов
     *
     * @return возвращает список всех контрагентов
     */
    List<Partner> getAllPartners();
}
