package sam.rus.bankapi.service.Impl;

import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.UserService;
import sam.rus.bankapi.util.PasswordEncryption;

import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository = new UserRepositoryImpl();

    public UserServiceImpl() {
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean addUser(User user) {
        user.setPassword(PasswordEncryption.hashedPassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        return userRepository.addUser(user);
    }

    @Override
    public long getUserIdByLogin(String login) throws UserNotFoundException {
        Optional<User> user = userRepository.getUserByLogin(login);
        if (user.isPresent()) {
            return user.get().getId();
        } else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean authentication(String login, String password) {
        boolean result = false;
        Optional<User> user = userRepository.getUserByLogin(login);
        if(user.isPresent()){
            User user1 = user.get();
            if(user1.getPassword().equals(password))
                result = true;
        }
        return result;
    }

    @Override
    public Role getRoleByLogin(String login) throws UserNotFoundException {
        Optional<User> user = userRepository.getUserByLogin(login);
        if (user.isPresent()) {
            return user.get().getRole();
        } else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public User getUserByLogin(String login) throws UserNotFoundException {
        Optional<User> user = userRepository.getUserByLogin(login);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException();
        }
    }
}
