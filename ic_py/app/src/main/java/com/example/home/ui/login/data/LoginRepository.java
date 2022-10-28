package com.example.home.ui.login.data;

import com.example.home.ui.login.data.model.LoggedInUser;
import com.example.home.ui.login.data.model.SigninUser;
import com.example.home.utils.Communicaton;

import java.io.IOException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {
    private static volatile Communicaton communicaton;
    private static volatile LoginRepository instance;

    private LoggedInUser user = null;
    private SigninUser suser = null;
    // private constructor : singleton access
    private LoginRepository( ) {
        communicaton = Communicaton.getInstance();
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    private void setSigninInUser(SigninUser suser) {
        this.suser = suser;
    }

    public Result<SigninUser> signin(String username, String password){
        Result<SigninUser> result;
        try {
            // TODO: handle loggedInUser authentication
            SigninUser user = new SigninUser(java.util.UUID.randomUUID().toString(), "Jane Doe");
            result =  new Result.Success<>(user);
        } catch (Exception e) {
            result =  new Result.Error(new IOException("Error logging in", e));
        }

        if (result instanceof Result.Success) {
            setSigninInUser(((Result.Success<SigninUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result;
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser user = new LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe");
            result =  new Result.Success<>(user);
        } catch (Exception e) {
            result =  new Result.Error(new IOException("Error logging in", e));
        }

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}