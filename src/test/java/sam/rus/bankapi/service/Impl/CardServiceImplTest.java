package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.entity.Card;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.CardNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.CardRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CardServiceImplTest {
    @Mock
    CardRepositoryImpl cardRepository = Mockito.mock(CardRepositoryImpl.class);
    @Mock
    UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    BillServiceImpl billService = Mockito.mock(BillServiceImpl.class);
    @InjectMocks
    CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addCard() throws UserNotFoundException, BillNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        String date = DateTimeFormatter.ofPattern("MM/yy", Locale.ENGLISH)
                .format(LocalDate.now().plusYears(2));
        Card card = new Card(
                date,
                user.getFirstName(),
                user.getLastName(),
                1);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.addCard(card)).thenReturn(true);
        assertTrue(cardService.addCard("login", 1));
    }

    @Test
    void addCardFalseAndAccess() throws UserNotFoundException, BillNotFoundException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        String date = DateTimeFormatter.ofPattern("MM/yy", Locale.ENGLISH)
                .format(LocalDate.now().plusYears(2));
        Card card = new Card(
                date,
                user.getFirstName(),
                user.getLastName(),
                1);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.addCard(card)).thenReturn(false);
        assertFalse(cardService.addCard("login", 1));
    }

    @Test
    void addCardNoAccess() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        assertFalse(cardService.addCard("login", 1));
    }

    @Test
    void addCardNotFoundBill() throws BillNotFoundException, UserNotFoundException {
        Mockito.when(billService.getBillById(1)).thenThrow(BillNotFoundException.class);
        assertFalse(cardService.addCard("login", 1));
    }
    @Test
    void addCardNotFoundUser() throws UserNotFoundException {
        Mockito.when(userService.getUserByLogin("login")).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> cardService.addCard("login", 1));
    }

    @Test
    void getCardById() throws BillNotFoundException, UserNotFoundException, CardNotFoundException, NoAccessException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        String date = DateTimeFormatter.ofPattern("MM/yy", Locale.ENGLISH)
                .format(LocalDate.now().plusYears(2));
        Card card = new Card(
                date,
                user.getFirstName(),
                user.getLastName(),
                1);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.getCardById(1)).thenReturn(Optional.of(card));
        assertEquals(Optional.of(card).get(), cardService.getCardById(1, "login"));
    }

    @Test
    void getCardNoAccess() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        String date = DateTimeFormatter.ofPattern("MM/yy", Locale.ENGLISH)
                .format(LocalDate.now().plusYears(2));
        Card card = new Card(
                date,
                user.getFirstName(),
                user.getLastName(),
                1);
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.getCardById(1)).thenReturn(Optional.of(card));
        assertThrows(NoAccessException.class, () -> cardService.getCardById(1, "login"));
    }

    @Test
    void getCardNotFoundBill() throws BillNotFoundException {
        Mockito.when(billService.getBillById(1)).thenThrow(BillNotFoundException.class);
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1, "login"));
    }

    @Test
    void getCardNotFoundUser() throws UserNotFoundException {
        Mockito.when(userService.getUserByLogin("login")).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> cardService.getCardById(1, "login"));
    }

    @Test
    void getCardNotFoundCard() {
        Mockito.when(cardRepository.getCardById(1)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1, "login"));
    }

    @Test
    void getAllCardsByBill() throws BillNotFoundException, UserNotFoundException, NoAccessException {
        Bill bill = new Bill(1, 1, 0, 1);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        cardList.add(new Card());
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.getAllCardsByBill(1)).thenReturn(cardList);
        assertEquals(cardList, cardService.getAllCardsByBill(1, "login"));
    }

    @Test
    void getAllCardsByBillNoAccess() throws BillNotFoundException, UserNotFoundException {
        Bill bill = new Bill(1, 1, 0, 2);
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        cardList.add(new Card());
        Mockito.when(billService.getBillById(1)).thenReturn(Optional.of(bill).get());
        Mockito.when(userService.getUserByLogin("login")).thenReturn(user);
        Mockito.when(cardRepository.getAllCardsByBill(1)).thenReturn(cardList);
        assertThrows(NoAccessException.class, () -> cardService.getAllCardsByBill(1, "login"));
    }

    @Test
    void getAllCardsByBillNotFoundBill() throws BillNotFoundException {
        Mockito.when(billService.getBillById(1)).thenThrow(BillNotFoundException.class);
        assertThrows(BillNotFoundException.class, () -> cardService.getAllCardsByBill( 1, "login"));
    }

    @Test
    void getAllCards() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        cardList.add(new Card());
        Mockito.when(cardRepository.getAllCards()).thenReturn(cardList);
        assertEquals(cardList, cardService.getAllCards());
    }

    @Test
    void getAllCardsByStatus() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        cardList.add(new Card());
        Mockito.when(cardRepository.getAllCardsByStatus("ACTIVE")).thenReturn(cardList);
        assertEquals(cardList, cardService.getAllCardsByStatus("ACTIVE"));
    }

    @Test
    void getAllCardsByStatusNull() {
        List<Card> cardList = new ArrayList<>();
        Mockito.when(cardRepository.getAllCardsByStatus("ACTIVE")).thenReturn(cardList);
        assertEquals(cardList, cardService.getAllCardsByStatus("ACTIVE"));
    }

    @Test
    void changeCardStatus() {
        Mockito.when(cardRepository.changeCardStatus(1, "ACTIVE")).thenReturn(true);
        assertTrue(cardService.changeCardStatus(1, "ACTIVE"));
    }

    @Test
    void changeCardStatusActiveFalse() {
        Mockito.when(cardRepository.changeCardStatus(1, "ACTIVE")).thenReturn(false);
        assertFalse(cardService.changeCardStatus(1, "ACTIVE"));
    }

    @Test
    void changeCardStatusNotActive() {
        Mockito.when(cardRepository.changeCardStatus(1, "NOT_ACTIVE")).thenReturn(false);
        assertFalse(cardService.changeCardStatus(1, "NOT_ACTIVE"));
    }

    @Test
    void changeCardStatusNotCorrect() {
        assertFalse(cardService.changeCardStatus(1, "bad"));
    }

}