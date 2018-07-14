package me.picknchew.teachassist.login;

public interface LoginView {

    void navigateToCourses();

    void showProgress(boolean show);

    void invalidCredentials();

    void networkError();

    void noNetworkError();
}
