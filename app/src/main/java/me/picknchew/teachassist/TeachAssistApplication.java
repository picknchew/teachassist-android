package me.picknchew.teachassist;

import android.app.Application;

import com.google.gson.Gson;

import me.picknchew.teachassist.auth.AuthManager;
import me.picknchew.teachassistapi.TeachAssistAPI;
import me.picknchew.teachassistapi.TeachAssistService;

public class TeachAssistApplication extends Application {
    private static TeachAssistApplication instance;

    private Gson gson;
    private CourseNameLookup courseNameLookup;
    private TeachAssistService teachAssistService;
    private AuthManager authManager;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        gson = new Gson();

        courseNameLookup = new CourseNameLookup();
        teachAssistService = new TeachAssistAPI().createService();
        authManager = new AuthManager(teachAssistService, getSharedPreferences("auth", MODE_PRIVATE));
    }

    public Gson getGson() {
        return gson;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public CourseNameLookup getCourseNameLookup() {
        return courseNameLookup;
    }

    public TeachAssistService getTeachAssistService() {
        return teachAssistService;
    }

    public static TeachAssistApplication getInstance() {
        return instance;
    }
}
