package me.picknchew.teachassist.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.TeachAssistApplication;
import me.picknchew.teachassist.auth.AuthManager;
import me.picknchew.teachassist.courses.CoursesActivity;
import me.picknchew.teachassist.util.NetworkUtil;

public class LoginActivity extends Activity implements LoginView {
    private AuthManager authManager;
    private LoginPresenter presenter;
    private EditText studentNumberView;
    private EditText passwordView;
    private AlertDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = TeachAssistApplication.getInstance().getAuthManager();

        if (authManager.isAuthenticated()) {
            navigateToCourses();
            return;
        }

        setContentView(R.layout.activity_login);

        renderLogo();

        presenter = new LoginPresenter(this, authManager);
        studentNumberView = (EditText) findViewById(R.id.student_id);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }

                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        createLoginDialog();
    }

    private void attemptLogin() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            noNetworkError();
            return;
        }

        presenter.authenticate(studentNumberView.getText().toString(), passwordView.getText().toString());
    }

    private void createLoginDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(R.layout.dialog_login);

        loginDialog = dialogBuilder.create();
    }

    private void renderLogo() {
        TextView logoView = (TextView) findViewById(R.id.logo);
        Spannable logo = new SpannableString("teachassist");

        // 'teach' coloring
        logo.setSpan(new ForegroundColorSpan(Color.rgb(43, 104, 79)), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // 'assist' coloring
        logo.setSpan(new ForegroundColorSpan(Color.rgb(7, 139, 86)), 5, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        logoView.setText(logo);
    }

    @Override
    public void navigateToCourses() {
        Intent intent = new Intent(this, CoursesActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    public void showProgress(final boolean show) {
        if (show) {
            loginDialog.show();
            return;
        }

        loginDialog.hide();
    }

    @Override
    public void invalidCredentials() {
        Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_LONG).show();
    }

    @Override
    public void noNetworkError() {
        Toast.makeText(this, R.string.error_no_network, Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkError() {
        Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG).show();
    }
}
