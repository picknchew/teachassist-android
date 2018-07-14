package me.picknchew.teachassist.courses.course;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.parceler.Parcels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.TeachAssistApplication;
import me.picknchew.teachassist.auth.AuthManager;
import me.picknchew.teachassist.courses.CourseInfoItemDecoration;
import me.picknchew.teachassist.util.Callback;
import me.picknchew.teachassist.util.NetworkUtil;
import me.picknchew.teachassistapi.model.Assignment;
import me.picknchew.teachassistapi.model.Course;
import me.picknchew.teachassistapi.model.CourseInfo;

public class CourseActivity extends Activity implements CourseView, SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    private TextView markTextView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView assignmentsView;

    private SharedPreferences sharedPreferences;

    private AssignmentAdapter adapter;

    private CourseInfo courseInfo;
    private Course course;
    private List<Assignment> assignments;

    private CoursePresenter presenter;
    private AuthManager authManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);

        toolbar = (Toolbar) findViewById(R.id.toolbar_course);
        markTextView = (TextView) findViewById(R.id.current_mark);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.course_refresh_layout);

        sharedPreferences = getSharedPreferences("courses", MODE_PRIVATE);

        courseInfo = Parcels.unwrap(getIntent().getParcelableExtra("courseInfo"));
        course = getCourseFromCache(courseInfo.getId());

        if (course != null) {
            assignments = course.getAssignments();
        } else {
            assignments = new ArrayList<>();
        }

        initToolbar();
        initRefreshLayout();
        initRecyclerView();
        updateMark();

        presenter = new CoursePresenter(this, assignments);
        authManager = TeachAssistApplication.getInstance().getAuthManager();

        updateCourseFromServer();
    }

    private void initToolbar() {
        //toolbar.setTitle(courseInfo.getCourseCode());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setDistanceToTriggerSync((int) (getResources().getDisplayMetrics().density * 120));
    }

    private void updateCourseFromServer() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.error_no_network, Toast.LENGTH_LONG).show();
            stopRefreshProgress();
            return;
        }

        if (!authManager.isSessionValid()) {
            authManager.renewSession(this, new Callback() {
                @Override
                public void onCall() {
                    // attempt to update course from web
                    presenter.updateCourse(authManager.getSession(), courseInfo);
                }
            });
        } else {
            presenter.updateCourse(authManager.getSession(), courseInfo);
        }
    }

    private Course getCourseFromCache(String id) {
        String serializedCourse = sharedPreferences.getString(id, null);

        if (serializedCourse == null) {
            return null;
        }

        return ((TeachAssistApplication) getApplication()).getGson().fromJson(serializedCourse, Course.class);
    }

    public void updateMark() {
        markTextView.setText(courseInfo.getMark());
    }

    private void initRecyclerView() {
        RecyclerView assignmentsView = (RecyclerView) findViewById(R.id.assignments);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        adapter = new AssignmentAdapter(assignments);

        assignmentsView.setHasFixedSize(true);
        assignmentsView.setLayoutManager(llm);
        assignmentsView.addItemDecoration(new CourseInfoItemDecoration(this));
        assignmentsView.setAdapter(adapter);
    }

    @Override
    public void updateCourses(List<CourseInfo> updatedCourses) {
        for (CourseInfo courseInfo : updatedCourses) {
            if (courseInfo.getId().equals(this.courseInfo.getId())) {
                this.courseInfo = courseInfo;
                updateMark();
            }
        }

        Type type = new TypeToken<List<CourseInfo>>(){}.getType();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("courses", ((TeachAssistApplication) getApplication()).getGson().toJson(updatedCourses, type));
        editor.apply();
    }

    @Override
    public void updateAssignments() {
        adapter.updateWeights();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void saveCourse(Course course) {
        this.course = course;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(courseInfo.getId(), TeachAssistApplication.getInstance().getGson().toJson(course));
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
    public void invalidCourseError() {
        Toast.makeText(this, R.string.error_invalid_course, Toast.LENGTH_LONG).show();
    }

    @Override
    public void stopRefreshProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        updateCourseFromServer();
    }
}
