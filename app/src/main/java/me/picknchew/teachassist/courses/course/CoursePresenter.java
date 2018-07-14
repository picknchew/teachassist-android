package me.picknchew.teachassist.courses.course;

import java.util.List;

import me.picknchew.teachassist.courses.CoursesInteractor;
import me.picknchew.teachassistapi.model.Assignment;
import me.picknchew.teachassistapi.model.Course;
import me.picknchew.teachassistapi.model.CourseInfo;
import me.picknchew.teachassistapi.model.Session;

public class CoursePresenter implements CourseInteractor.Listener, CoursesInteractor.Listener {
    private final CourseView view;
    private final CourseInteractor interactor;
    private final CoursesInteractor coursesInteractor;
    private final List<Assignment> assignments;

    public CoursePresenter(CourseView view, List<Assignment> assignments) {
        this.view = view;
        this.interactor = new CourseInteractor(this);
        this.coursesInteractor = new CoursesInteractor(this);
        this.assignments = assignments;
    }

    public void updateCourse(Session session, CourseInfo courseInfo) {
        coursesInteractor.updateCourses(session);
        interactor.updateCourse(session, courseInfo);
    }

    private void updateView(List<Assignment> updatedAssignments) {
        this.assignments.clear();
        this.assignments.addAll(updatedAssignments);

        view.updateAssignments();
    }


    @Override
    public void onUpdate(Course course) {
        updateView(course.getAssignments());
        view.saveCourse(course);
        view.stopRefreshProgress();
    }

    @Override
    public void onUpdate(List<CourseInfo> courses) {
        view.updateCourses(courses);
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

    @Override
    public void onInvalidCourseError() {
        view.invalidCourseError();
        view.stopRefreshProgress();
    }
}
