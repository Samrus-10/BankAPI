package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.BillRepositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

public class BillServiceImplTest {
    @Mock
    private final BillRepositoryImpl billRepository = Mockito.mock(BillRepositoryImpl.class);
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @InjectMocks
    private BillServiceImpl billService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addBillTrue() {
        Mockito.when(billRepository.addBill(1)).thenReturn(true);
        assertTrue(billService.addBill(1));
    }

    @Test
    void addBillFalse() {
        Mockito.when(billRepository.addBill(1)).thenReturn(false);
        assertFalse(billService.addBill(1));
    }

    @Test
    public void getBillByIdCorrect() throws BillNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        Mockito.when(billRepository.getBillById(1)).thenReturn(Optional.of(bill));
        assertEquals(Optional.of(bill).get(), billService.getBillById(1));

    }

    @Test
    public void getBillByIdException() {
        Mockito.when(billRepository.getBillById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BillNotFoundException.class, () -> billService.getBillById(2));
    }

    @Test
    public void getBillByIdAndLoginCorrectAndAccess()
            throws UserNotFoundException, BillNotFoundException, NoAccessException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billRepository.getBillById(1)).thenReturn(Optional.of(bill));
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertEquals(bill, billService.getBillByIdAndLogin(1, "login"));
    }

    @Test
    public void getBillByIdAndLoginNotFound() {
        Mockito.when(billRepository.getBillById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BillNotFoundException.class, () -> billService.getBillByIdAndLogin(1, "login"));
    }

    @Test
    public void getBillByIdAndLoginNoAccess() throws UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billRepository.getBillById(1)).thenReturn(Optional.of(bill));
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertThrows(NoAccessException.class, () -> billService.getBillByIdAndLogin(1, "login"));
    }

    @Test
    public void getAllBillsByUserCorrect() {
        List<Bill> billList = new ArrayList<>();
        billList.add(new Bill());
        billList.add(new Bill());
        Mockito.when(billRepository.getAllBillsByUser(1)).thenReturn(billList);
        assertEquals(billList, billService.getAllBillsByUser(1));
    }

    @Test
    public void getAllBillsByUserNull() {
        Mockito.when(billRepository.getAllBillsByUser(1)).thenReturn(null);
        assertNull(billService.getAllBillsByUser(1));
    }

    @Test
    public void plusBalance() {
        Mockito.when(billRepository.plusBalance(1, 100)).thenReturn(true);
        assertTrue(billService.plusBalance(1, 100));
    }

    @Test
    public void minusBalance() {
        Mockito.when(billRepository.minusBalance(1, 100)).thenReturn(true);
        assertTrue(billService.minusBalance(1, 100));
    }

    @Test
    public void getBalanceCorrectAndAccess() throws UserNotFoundException, NoAccessException, BillNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billRepository.getBillById(1)).thenReturn(Optional.of(bill));
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertEquals(0, billService.getBalance(1, "login"));
    }

    @Test
    public void getBalanceNotFound() {
        Mockito.when(billRepository.getBillById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BillNotFoundException.class, () -> billService.getBalance(1, "login"));
    }

    @Test
    public void getBalanceNoAccess() throws UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billRepository.getBillById(1)).thenReturn(Optional.of(bill));
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertThrows(NoAccessException.class, () -> billService.getBalance(1, "login"));
    }

}