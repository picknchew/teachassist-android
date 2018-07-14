package me.picknchew.teachassist.courses;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.parceler.Parcels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.TeachAssistApplication;
import me.picknchew.teachassist.auth.AuthManager;
import me.picknchew.teachassist.courses.course.CourseActivity;
import me.picknchew.teachassist.util.Callback;
import me.picknchew.teachassist.util.NetworkUtil;
import me.picknchew.teachassistapi.model.CourseInfo;

public class CoursesActivity extends Activity implements CoursesView, SwipeRefreshLayout.OnRefreshListener {
    private AuthManager authManager;

    private TextView averageTextView;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout drawerLayout;

    private SharedPreferences sharedPreferences;
    private CourseInfoAdapter adapter;
    private CoursesPresenter presenter;
    private List<CourseInfo> courses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_courses);

        authManager = TeachAssistApplication.getInstance().getAuthManager();

        averageTextView = (TextView) findViewById(R.id.average_mark);
        toolbar = (Toolbar) findViewById(R.id.toolbar_courses);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.courses_refresh_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        sharedPreferences = getSharedPreferences("courses", MODE_PRIVATE);

        courses = getCoursesFromCache();
        presenter = new CoursesPresenter(this, courses);

        initToolbar();
        initDrawer();
        initRecyclerView();
        initRefreshLayout();
        updateAverageMark();
        updateCoursesFromServer();
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    private void initRefreshLayout() {
        refreshLayout.setDistanceToTriggerSync((int) (getResources().getDisplayMetrics().density * 120));
        refreshLayout.setOnRefreshListener(this);
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // push parent view to the right
                super.onDrawerSlide(drawerView, slideOffset);
                refreshLayout.setTranslationX(slideOffset * drawerView.getWidth());
            }
        };

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        authManager.logout(CoursesActivity.this);
                    default:
                        return true;
                }
            }
        });
    }

    private void updateCoursesFromServer() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.error_no_network, Toast.LENGTH_LONG).show();
            stopRefreshProgress();
            return;
        }

        if (!authManager.isSessionValid()) {
            authManager.renewSession(this, new Callback() {
                @Override
                public void onCall() {
                    // attempt to update courses from web
                    presenter.updateCourses(authManager.getSession());
                }
            });
        } else {
            presenter.updateCourses(authManager.getSession());
        }
    }

    private void initRecyclerView() {
        RecyclerView coursesView = (RecyclerView) findViewById(R.id.courses);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        adapter = new CourseInfoAdapter(courses, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToCourse((CourseInfo) view.getTag());
            }
        });

        coursesView.setHasFixedSize(true);
        coursesView.setLayoutManager(llm);
        coursesView.addItemDecoration(new CourseInfoItemDecoration(this));
        coursesView.setAdapter(adapter);
    }

    private List<CourseInfo> getCoursesFromCache() {
        Type type = new TypeToken<List<CourseInfo>>() {
        }.getType();
        String serializedCourses = sharedPreferences.getString("courses", null);

        if (serializedCourses == null) {
            return new ArrayList<>();
        }

        return ((TeachAssistApplication) getApplication()).getGson().fromJson(serializedCourses, type);
    }

    @Override
    public void updateAverageMark() {
        int numCourses = 0;
        double total = 0;

        for (CourseInfo course : courses) {
            if (course.isMarkHidden()) {
                continue;
            }

            total += Double.parseDouble(course.getMark().replace("%", ""));
            numCourses++;
        }

        averageTextView.setText(String.format(Locale.US, "%.1f%%", numCourses > 0 ? total / numCourses : 0.0D));
    }

    @Override
    public void navigateToCourse(CourseInfo courseInfo) {
        Intent intent = new Intent(this, CourseActivity.class);

        intent.putExtra("courseInfo", Parcels.wrap(courseInfo));
        startActivity(intent);
    }

    @Override
    public void stopRefreshProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void updateCourses(DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(adapter);
    }

    @Override
    public void saveCourses() {
        Type type = new TypeToken<List<CourseInfo>>() {
        }.getType();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("courses", ((TeachAssistApplication) getApplication()).getGson().toJson(courses, type));
        editor.apply();
    }

    @Override
    public void networkError() {
        Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sessionError() {
        Toast.makeText(this, R.string.error_retrieve, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        updateCoursesFromServer();
    }
}
