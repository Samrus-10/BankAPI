package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @Mock
    UserRepositoryImpl userRepository = Mockito.mock(UserRepositoryImpl.class);
    @InjectMocks
    UserServiceImpl userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addUser()  {
        Mockito.when(userRepository.addUser(Mockito.any())).thenReturn(true);
        assertTrue(userService.addUser(new User()));
    }

    @Test
    void addUserFalse()  {
        Mockito.when(userRepository.addUser(Mockito.any())).thenReturn(false);
        assertFalse(userService.addUser(new User()));
    }

    @Test
    void getUserIdByLogin() throws UserNotFoundException {
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.of(user));
        assertEquals(1, userService.getUserIdByLogin("login"));
    }

    @Test
    void getUserIdByLoginNull() {
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserIdByLogin("login"));
    }

    @Test
    void authentication() {
        assertFalse(userService.authentication("login", "123"));
    }

    @Test
    void getRoleByLogin() throws UserNotFoundException {
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user).get().getRole(), userService.getRoleByLogin("login"));
    }

    @Test
    void getRoleByLoginNull() {
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getRoleByLogin("login"));
    }

    @Test
    void getUserByLogin() throws UserNotFoundException {
        User user = new User(
                1, "login", "p", "f", "l",
                "m", "pa", "mb", Role.USER);
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user).get(), userService.getUserByLogin("login"));
    }

    @Test
    void getUserByLoginNull() {
        Mockito.when(userRepository.getUserByLogin("login")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByLogin("login"));
    }
}