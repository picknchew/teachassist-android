package me.picknchew.teachassist.auth;

import me.picknchew.teachassistapi.model.Session;

public abstract class AuthListener {

    public void onAuthenticated(String username, String password, Session session) {}
    public void onInvalidCredentials() {}
    public void onError() {}
}
