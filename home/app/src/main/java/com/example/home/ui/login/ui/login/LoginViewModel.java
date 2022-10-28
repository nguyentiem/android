package com.example.home.ui.login.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.home.R;
import com.example.home.ui.login.data.LoginRepository;
import com.example.home.ui.login.data.Result;
import com.example.home.ui.login.data.model.LoggedInUser;
import com.example.home.ui.login.data.model.SigninUser;


public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private MutableLiveData<Integer> login = new MutableLiveData<>();
    private MutableLiveData<Boolean> siginSuccess = new MutableLiveData<>();
    int loginStatus = 0;
    private MutableLiveData<Boolean> siginDone = new MutableLiveData<>();
//    private MutableLiveData<LoginFormState> signinFormState = new MutableLiveData<>();
//    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
//    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        login.postValue(loginStatus);
    }



    public void changeMode(int i){
       login.postValue(i);
    }

    LiveData<Integer> getStatusLayout() {
        return login;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void signin(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<SigninUser> result= loginRepository.signin(username, password);
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            siginSuccess.postValue(true);
            login.postValue(StatusMode.LOGIN);
        } else {
            siginSuccess.postValue(false);
            login.postValue(StatusMode.SIGNIN);
        }
    }

    public void logout(){
       if(loginRepository.isLoggedIn()){
           loginRepository.logout();
           login.postValue(StatusMode.LOGIN);
       }else{

       }
    }

      //////// login status change
    public void typeDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}