package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.OperationNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.OperationRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OperationServiceImplTest {
    @Mock
    OperationRepositoryImpl operationRepository = Mockito.mock(OperationRepositoryImpl.class);
    @Mock
    BillServiceImpl billService = Mockito.mock(BillServiceImpl.class);
    @Mock
    UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @InjectMocks
    OperationServiceImpl operationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addOperationTrue() throws BillNotFoundException {
        Bill bill = new Bill(1, 1, 1000, 1);
        Operation operation = new Operation(1, 1, 100);
        Mockito.when(billService.getBillById(Mockito.anyLong())).thenReturn(bill);
        Mockito.when(operationRepository.addOperation(Mockito.any())).thenReturn(true);
        Mockito.when(billService.minusBalance(Mockito.anyLong(), Mockito.anyDouble())).thenReturn(true);
        assertTrue(operationService.addOperation(operation));
    }

    @Test
    void addOperationFalseAddOperation() throws BillNotFoundException {
        Bill bill = new Bill(1, 1, 1000, 1);
        Operation operation = new Operation(1, 1, 100);
        Mockito.when(billService.getBillById(Mockito.anyLong())).thenReturn(bill);
        Mockito.when(operationRepository.addOperation(Mockito.any())).thenReturn(false);
        assertFalse(operationService.addOperation(operation));
    }

    @Test
    void addOperationFalseMinusBalance() throws BillNotFoundException {
        Bill bill = new Bill(1, 1, 1000, 1);
        Operation operation = new Operation(1, 1, 100);
        Mockito.when(billService.getBillById(Mockito.anyLong())).thenReturn(bill);
        Mockito.when(operationRepository.addOperation(Mockito.any())).thenReturn(true);
        Mockito.when(billService.minusBalance(Mockito.anyLong(), Mockito.anyDouble())).thenReturn(false);
        assertFalse(operationService.addOperation(operation));
    }

    @Test
    void addOperationFalse() throws BillNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        Operation operation = new Operation(1, 1, 100);
        Mockito.when(billService.getBillById(Mockito.anyLong())).thenReturn(bill);
        Mockito.when(operationRepository.addOperation(Mockito.any())).thenReturn(true);
        assertFalse(operationService.addOperation(operation));
    }

    @Test
    void getAllOperationsByBillId()
            throws BillNotFoundException, UserNotFoundException, OperationNotFoundException, NoAccessException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Operation> operationList = new ArrayList<>();
        operationList.add(new Operation());
        operationList.add(new Operation());
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(operationRepository.getAllOperationByBill(1)).thenReturn(operationList);
        assertEquals(operationList, operationService.getAllOperationsByBillId(1, "login"));
    }

    @Test
    void getAllOperationsByBillIdNotFound() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Operation> operationList = new ArrayList<>();
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(operationRepository.getAllOperationByBill(1)).thenReturn(operationList);
        assertThrows(OperationNotFoundException.class, () -> operationService.getAllOperationsByBillId(1, "login"));
    }

    @Test
    void getAllOperationByBillIdNoAccess() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertThrows(NoAccessException.class, () -> operationService.getAllOperationsByBillId(1, "login"));
    }

    @Test
    void getAllOperations() {
        List<Operation> operationList = new ArrayList<>();
        operationList.add(new Operation());
        operationList.add(new Operation());
        Mockito.when(operationRepository.getAllOperation()).thenReturn(operationList);
        assertEquals(operationList, operationService.getAllOperations());
    }

    @Test
    void getAllOperationsByStatus() {
        List<Operation> operationList = new ArrayList<>();
        operationList.add(new Operation());
        operationList.add(new Operation());
        Mockito.when(operationRepository.getAllOperationsByStatus("UNAPPROVED")).thenReturn(operationList);
        assertEquals(operationList, operationService.getAllOperationsByStatus("UNAPPROVED"));
    }

    @Test
    void changeStatusOperation() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "UNAPPROVED");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        Mockito.when(operationRepository.changeOperationStatus(1, "APPROVED")).thenReturn(true);
        assertTrue(operationService.changeStatusOperation(1, "APPROVED"));
    }

    @Test
    void changeStatusOperationToDeclineWhenApproved() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "DECLINE");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        assertFalse(operationService.changeStatusOperation(1, "APPROVED"));
    }

    @Test
    void changeStatusOperationToApprovedWhenDecline() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "APPROVED");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        assertFalse(operationService.changeStatusOperation(1, "DECLINE"));
    }

    @Test
    void changeStatusOperationDecline() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "UNAPPROVED");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        Mockito.when(billService.plusBalance(1, 100)).thenReturn(true);
        Mockito.when(operationRepository.changeOperationStatus(1, "DECLINE")).thenReturn(true);
        assertTrue(operationService.changeStatusOperation(1, "DECLINE"));
    }

    @Test
    void changeStatusOperationFail() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "UNAPPROVED");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        Mockito.when(billService.plusBalance(1, 100)).thenReturn(false);
        assertFalse(operationService.changeStatusOperation(1, "DECLINE"));
    }

    @Test
    void changeStatusOperationNotFound() {
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.empty());
        assertThrows(OperationNotFoundException.class,
                () -> operationService.changeStatusOperation(1, "APPROVED"));
    }

    @Test
    void changeStatusOperationBadRequest() throws OperationNotFoundException {
        Operation operation = new Operation(1,1,2,100, "UNAPPROVED");
        Mockito.when(operationRepository.getOperationById(1)).thenReturn(Optional.of(operation));
        assertFalse(operationService.changeStatusOperation(1, "BAD"));
    }
}