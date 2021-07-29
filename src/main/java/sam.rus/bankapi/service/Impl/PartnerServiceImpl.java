package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.util.exception.PartnerNotFoundException;
import sam.rus.bankapi.repository.Impl.PartnerRepositoryImpl;
import sam.rus.bankapi.repository.PartnerRepository;
import sam.rus.bankapi.service.PartnerService;

import java.util.List;

public class PartnerServiceImpl implements PartnerService {
    private PartnerRepository partnerRepository = new PartnerRepositoryImpl();

    public PartnerServiceImpl() {
    }

    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public boolean addPartner(Partner partner) {
        return partnerRepository.addPartner(partner);
    }

    @Override
    public List<Partner> getAllPartners() throws PartnerNotFoundException {
        List<Partner> partnerList = partnerRepository.getAllPartners();
        if (!partnerList.isEmpty()) {
            return partnerList;
        } else {
            throw new PartnerNotFoundException();
        }
    }
}
