package me.picknchew.teachassist.courses;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.databinding.CardCourseInfoBinding;
import me.picknchew.teachassistapi.model.CourseInfo;

public class CourseInfoAdapter extends RecyclerView.Adapter<CourseInfoAdapter.ViewHolder> {
    private final List<CourseInfo> courses;
    private final View.OnClickListener clickListener;

    CourseInfoAdapter(List<CourseInfo> courses, View.OnClickListener clickListener) {
        this.courses = courses;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardCourseInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.card_course_info, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CourseInfo courseInfo = courses.get(i);
        View view = viewHolder.itemView;

        viewHolder.bind(courseInfo);
        view.setTag(courseInfo);
        view.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardCourseInfoBinding binding;

        private ViewHolder(CardCourseInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(CourseInfo course) {
            binding.setCourseInfo(course);
            binding.executePendingBindings();
        }
    }
}
