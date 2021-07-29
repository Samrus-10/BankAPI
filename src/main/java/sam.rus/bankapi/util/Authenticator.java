package sam.rus.bankapi.util;

import sam.rus.bankapi.service.Impl.UserServiceImpl;
import com.sun.net.httpserver.BasicAuthenticator;


public class Authenticator extends BasicAuthenticator {
    private UserServiceImpl userServiceImpl = new UserServiceImpl();

    public Authenticator() {
        super("realm");
    }

    public Authenticator(UserServiceImpl userServiceImpl) {
        super("realm");
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public boolean checkCredentials(String login, String password) {
        return userServiceImpl.authentication(login, password);
    }
}
