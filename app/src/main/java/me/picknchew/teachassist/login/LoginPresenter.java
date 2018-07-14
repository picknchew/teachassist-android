package me.picknchew.teachassist.login;

import me.picknchew.teachassist.auth.AuthListener;
import me.picknchew.teachassist.auth.AuthManager;
import me.picknchew.teachassistapi.model.Session;

public class LoginPresenter extends AuthListener {
    private final LoginView view;
    private final AuthManager authManager;

    public LoginPresenter(LoginView view, AuthManager authManager) {
        this.view = view;
        this.authManager = authManager;
    }

    public void authenticate(String studentNumber, String password) {
        view.showProgress(true);
        authManager.authenticate(studentNumber, password, this);
    }

    @Override
    public void onAuthenticated(String studentNumber, String password, Session session) {
        authManager.setSession(session);
        authManager.setCredentials(studentNumber, password);

        view.showProgress(false);
        view.navigateToCourses();
    }

    @Override
    public void onInvalidCredentials() {
        view.showProgress(false);
        view.invalidCredentials();
    }

    @Override
    public void onError() {
        view.showProgress(false);
        view.networkError();
    }
}
