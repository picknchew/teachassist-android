package me.picknchew.teachassist.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.login.LoginActivity;
import me.picknchew.teachassistapi.TeachAssistService;
import me.picknchew.teachassistapi.model.Session;
import me.picknchew.teachassistapi.requests.AuthenticationRequest;
import me.picknchew.teachassistapi.responses.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private final TeachAssistService service;
    private final SharedPreferences sharedPreferences;

    private String studentNumber;
    private String password;

    private Session session;
    private long sessionExpiry;

    public AuthManager(TeachAssistService service, SharedPreferences sharedPreferences) {
        this.service = service;
        this.sharedPreferences = sharedPreferences;

        loadCredentialsFromCache();
    }

    public void setCredentials(String studentNumber, String password) {
        this.studentNumber = studentNumber;
        this.password = password;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("student_number", studentNumber);
        editor.putString("password", password);

        editor.apply();
    }

    private void loadCredentialsFromCache() {
        studentNumber = sharedPreferences.getString("student_number", null);
        password = sharedPreferences.getString("password", null);
    }

    public void renewSession(final Activity activity, final me.picknchew.teachassist.util.Callback success) {
        authenticate(studentNumber, password, new AuthListener() {
            @Override
            public void onAuthenticated(String username, String password, Session session) {
                setSession(session);
                success.onCall();
            }

            @Override
            public void onInvalidCredentials() {
                logout(activity);
            }

            @Override
            public void onError() {
                Toast.makeText(activity, R.string.error_network, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void authenticate(final String studentNumber, final String password, final AuthListener listener) {
        service.authenticate(new AuthenticationRequest(studentNumber, password)).enqueue(new Callback<BaseResponse<Session>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<Session>> call, @NonNull Response<BaseResponse<Session>> response) {
                BaseResponse<Session> baseResponse = response.body();

                if (baseResponse == null || baseResponse.hasError()) {
                    listener.onInvalidCredentials();
                    return;
                }

                listener.onAuthenticated(studentNumber, password, baseResponse.get());
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<Session>> call, @NonNull Throwable t) {
                listener.onError();
            }
        });
    }

    public boolean isSessionValid() {
        return session != null && sessionExpiry > System.currentTimeMillis();
    }

    public boolean isAuthenticated() {
        return studentNumber != null && password != null;
    }

    public void setSession(Session session) {
        this.session = session;
        // session only lasts for 10 minutes.
        this.sessionExpiry = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
    }

    public Session getSession() {
        return session;
    }

    public void logout(Activity activity) {
        studentNumber = null;
        password = null;

        // clear data
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        SharedPreferences.Editor coursesEditor = activity.getSharedPreferences("courses", Context.MODE_PRIVATE).edit();

        coursesEditor.clear();
        coursesEditor.apply();

        Intent intent = new Intent(activity, LoginActivity.class);

        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
        activity.finish();
    }
}
