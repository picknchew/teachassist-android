package me.picknchew.teachassist.courses.course;

import android.support.v7.util.DiffUtil;

import java.util.List;

import me.picknchew.teachassistapi.model.Course;
import me.picknchew.teachassistapi.model.CourseInfo;

public interface CourseView {

    void updateCourses(List<CourseInfo> updatedCourses);

    void updateAssignments();

    void saveCourse(Course course);

    void networkError();

    void sessionError();

    void invalidCourseError();

    void stopRefreshProgress();
}
