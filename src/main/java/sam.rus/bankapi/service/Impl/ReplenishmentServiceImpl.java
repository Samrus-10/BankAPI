package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.ReplenishmentRepositoryImpl;
import sam.rus.bankapi.repository.ReplenishmentRepository;
import sam.rus.bankapi.service.BillService;
import sam.rus.bankapi.service.ReplenishmentService;
import sam.rus.bankapi.service.UserService;

import java.util.List;

public class ReplenishmentServiceImpl implements ReplenishmentService {
    private ReplenishmentRepository replenishmentRepository = new ReplenishmentRepositoryImpl();
    private BillService billService = new BillServiceImpl();
    private UserService userService = new UserServiceImpl();

    public ReplenishmentServiceImpl() {
    }

    public ReplenishmentServiceImpl(ReplenishmentRepository replenishmentRepository, BillService billService, UserService userService) {
        this.replenishmentRepository = replenishmentRepository;
        this.billService = billService;
        this.userService = userService;
    }

    @Override
    public boolean addReplenishment(Replenishment replenishment) {
        if (replenishment.getSum() > 0) {
            if (billService.plusBalance(replenishment.getBillId(), replenishment.getSum())) {
                return replenishmentRepository.addReplenishment(replenishment);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Replenishment> getAllReplenishmentByBill(long id, String login)
            throws BillNotFoundException, NoAccessException, UserNotFoundException {
        User user = userService.getUserByLogin(login);
        Bill bill = billService.getBillById(id);
        if (user.getId() == bill.getUserId()) {
            return replenishmentRepository.getAllReplenishmentByBill(id);
        } else {
            throw new NoAccessException();
        }
    }
}
