package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.OperationStatus;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.OperationNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.OperationRepositoryImpl;
import sam.rus.bankapi.repository.OperationRepository;
import sam.rus.bankapi.service.BillService;
import sam.rus.bankapi.service.OperationService;
import sam.rus.bankapi.service.UserService;

import java.util.List;
import java.util.Optional;

public class OperationServiceImpl implements OperationService {
    private OperationRepository operationRepository = new OperationRepositoryImpl();
    private BillService billService = new BillServiceImpl();
    private UserService userService = new UserServiceImpl();

    public OperationServiceImpl() {
    }

    public OperationServiceImpl(OperationRepository operationRepository, BillService billService, UserService userService) {
        this.operationRepository = operationRepository;
        this.billService = billService;
        this.userService = userService;
    }

    @Override
    public boolean addOperation(Operation operation) throws BillNotFoundException {
        Bill bill = billService.getBillById(operation.getSourceId());
        if (bill.getBalance() - operation.getSum() >= 0 && bill.getBalance() > 0) {
            if (operationRepository.addOperation(operation)) {
                return billService.minusBalance(bill.getId(), operation.getSum());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Operation> getAllOperationsByBillId(long id, String login)
            throws OperationNotFoundException, NoAccessException, BillNotFoundException, UserNotFoundException {
        User user = userService.getUserByLogin(login);
        Bill bill = billService.getBillById(id);
        if (user.getId() == bill.getUserId()) {
            List<Operation> operationList = operationRepository.getAllOperationByBill(id);
            if (!operationList.isEmpty()) {
                return operationList;
            } else {
                throw new OperationNotFoundException();
            }
        } else {
            throw new NoAccessException();
        }
    }

    @Override
    public List<Operation> getAllOperations() {
        return operationRepository.getAllOperation();
    }

    @Override
    public List<Operation> getAllOperationsByStatus(String status) {
        return operationRepository.getAllOperationsByStatus(status.toUpperCase());
    }

    @Override
    public boolean changeStatusOperation(long id, String status) throws OperationNotFoundException {
        Optional<Operation> operation = operationRepository.getOperationById(id);
        if (operation.isPresent()) {
            String statusUpdatableOperation = operation.get().getStatus();
            if (status.toUpperCase().equals(OperationStatus.APPROVED.toString())) {
                if (statusUpdatableOperation.toUpperCase().equals(OperationStatus.DECLINE.toString())) {
                    return false;
                } else {
                    operationRepository.changeOperationStatus(id, status.toUpperCase());
                    return true;
                }
            } else if (status.toUpperCase().equals(OperationStatus.DECLINE.toString())) {
                if (statusUpdatableOperation.toUpperCase().equals(OperationStatus.APPROVED.toString())) {
                    return false;
                } else {
                    if (billService.plusBalance(operation.get().getSourceId(), operation.get().getSum())) {
                        return operationRepository.changeOperationStatus(id, status.toUpperCase());
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            throw new OperationNotFoundException();
        }
    }
}
