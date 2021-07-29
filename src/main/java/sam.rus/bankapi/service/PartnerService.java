package sam.rus.bankapi.service;

import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.util.exception.PartnerNotFoundException;

import java.util.List;

/**
 * Слой для получения данных из репозитория и отправки на контроллер
 */
public interface PartnerService {
    /**
     * Запрос к репозиторию для создания нового контрагента
     *
     * @param partner данные контрагента
     * @return возвращает true - при создании, false - при ошибке
     */
    boolean addPartner(Partner partner);

    /**
     * Запрос к репозиторию для получения списка всех контрагентов
     *
     * @return возвращает список всех контрагентов
     * @throws PartnerNotFoundException если контрагентов нет
     */
    List<Partner> getAllPartners() throws PartnerNotFoundException;
}
