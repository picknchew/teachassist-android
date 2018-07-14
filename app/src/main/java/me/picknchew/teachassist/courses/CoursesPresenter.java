package me.picknchew.teachassist.courses;

import android.support.v7.util.DiffUtil;

import java.util.List;

import me.picknchew.teachassistapi.model.CourseInfo;
import me.picknchew.teachassistapi.model.Session;

public class CoursesPresenter implements CoursesInteractor.Listener {
    private final CoursesInteractor interactor;
    private final CoursesView view;
    private final List<CourseInfo> courses;

    public CoursesPresenter(CoursesView view, List<CourseInfo> courses) {
        this.interactor = new CoursesInteractor(this);
        this.view = view;
        this.courses = courses;
    }

    public void updateCourses(Session session) {
        interactor.updateCourses(session);
    }

    private void updateView(List<CourseInfo> updatedCourses) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CoursesDiffUtilCallback(courses, updatedCourses));

        view.updateCourses(diffResult);

        this.courses.clear();
        this.courses.addAll(updatedCourses);

        view.updateAverageMark();
        view.saveCourses();
    }

    @Override
    public void onUpdate(List<CourseInfo> courses) {
        updateView(courses);
        view.stopRefreshProgress();
    }

    @Override
    public void onNetworkError() {
        view.networkError();
        view.stopRefreshProgress();
    }

    @Override
    public void onSessionError() {
        view.sessionError();
        view.stopRefreshProgress();
    }
}
