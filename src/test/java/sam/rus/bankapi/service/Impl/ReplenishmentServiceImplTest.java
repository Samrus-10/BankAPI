package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.ReplenishmentRepositoryImpl;
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

class ReplenishmentServiceImplTest {
    @Mock
    ReplenishmentRepositoryImpl replenishmentRepository;
    @Mock
    BillServiceImpl billService;
    @Mock
    UserServiceImpl userService;
    @InjectMocks
    ReplenishmentServiceImpl replenishmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addReplenishment() {
        Replenishment replenishment = new Replenishment(100, 1);
        Mockito.when(billService.plusBalance(Mockito.anyLong(), Mockito.anyDouble())).thenReturn(true);
        Mockito.when(replenishmentRepository.addReplenishment(Mockito.any())).thenReturn(true);
        assertTrue(replenishmentService.addReplenishment(replenishment));
    }

    @Test
    void addReplenishmentFalseRepository() {
        Replenishment replenishment = new Replenishment(100, 1);
        Mockito.when(billService.plusBalance(Mockito.anyLong(), Mockito.anyDouble())).thenReturn(true);
        Mockito.when(replenishmentRepository.addReplenishment(Mockito.any())).thenReturn(false);
        assertFalse(replenishmentService.addReplenishment(replenishment));
    }

    @Test
    void addReplenishmentFalsePlusBalance() {
        Replenishment replenishment = new Replenishment(100, 1);
        Mockito.when(billService.plusBalance(Mockito.anyLong(), Mockito.anyDouble())).thenReturn(false);
        assertFalse(replenishmentService.addReplenishment(replenishment));
    }

    @Test
    void addReplenishmentFalseGetSum() {
        Replenishment replenishment = new Replenishment(0, 1);
        assertFalse(replenishmentService.addReplenishment(replenishment));
    }

    @Test
    void getAllReplenishmentByBill() throws BillNotFoundException, UserNotFoundException, NoAccessException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Replenishment> replenishmentList = new ArrayList<>();
        replenishmentList.add(new Replenishment());
        replenishmentList.add(new Replenishment());
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(replenishmentRepository.getAllReplenishmentByBill(1)).thenReturn(replenishmentList);
        assertEquals(replenishmentList, replenishmentService.getAllReplenishmentByBill(1, "login"));
    }

    @Test
    void getAllReplenishmentByBillNoAccess() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Replenishment> replenishmentList = new ArrayList<>();
        replenishmentList.add(new Replenishment());
        replenishmentList.add(new Replenishment());
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(replenishmentRepository.getAllReplenishmentByBill(1)).thenReturn(replenishmentList);
        assertThrows(NoAccessException.class, () -> replenishmentService.getAllReplenishmentByBill(1, "login"));
    }
}