package me.picknchew.teachassist.courses;

import android.support.v7.util.DiffUtil;

import me.picknchew.teachassistapi.model.CourseInfo;

public interface CoursesView {

    void navigateToCourse(CourseInfo courseInfo);

    void updateCourses(DiffUtil.DiffResult diffResult);

    void saveCourses();

    void networkError();

    void sessionError();

    void stopRefreshProgress();

    void updateAverageMark();
}
