package me.picknchew.teachassist.courses;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

import me.picknchew.teachassistapi.model.CourseInfo;

public class CoursesDiffUtilCallback extends DiffUtil.Callback {
    private final List<CourseInfo> oldCourses;
    private final List<CourseInfo> newCourses;

    public CoursesDiffUtilCallback(List<CourseInfo> oldCourses, List<CourseInfo> newCourses) {
        this.oldCourses = oldCourses;
        this.newCourses = newCourses;
    }

    @Override
    public int getOldListSize() {
        return oldCourses.size();
    }

    @Override
    public int getNewListSize() {
        return newCourses.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCourses.get(oldItemPosition).getId().equals(newCourses.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCourses.get(oldItemPosition).equals(newCourses.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
