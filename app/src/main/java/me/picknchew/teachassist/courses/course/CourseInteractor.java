package me.picknchew.teachassist.courses.course;

import android.support.annotation.NonNull;

import java.util.List;

import me.picknchew.teachassist.TeachAssistApplication;
import me.picknchew.teachassistapi.TeachAssistService;
import me.picknchew.teachassistapi.model.Course;
import me.picknchew.teachassistapi.model.CourseInfo;
import me.picknchew.teachassistapi.model.Session;
import me.picknchew.teachassistapi.requests.CourseRequest;
import me.picknchew.teachassistapi.responses.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseInteractor {
    private final TeachAssistService service = TeachAssistApplication.getInstance().getTeachAssistService();
    private final Listener listener;

    public CourseInteractor(Listener listener) {
        this.listener = listener;
    }

    public void updateCourse(Session session, CourseInfo info) {
        service.getCourse(new CourseRequest(session, info)).enqueue(new Callback<BaseResponse<Course>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<Course>> call, @NonNull Response<BaseResponse<Course>> response) {
                BaseResponse<Course> baseResponse = response.body();

                if (baseResponse == null || baseResponse.hasError()) {
                    listener.onSessionError();
                    return;
                }

                listener.onUpdate(baseResponse.get());
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<Course>> call, @NonNull Throwable t) {
                listener.onNetworkError();
            }
        });

    }

    public interface Listener {
        void onUpdate(Course course);

        void onNetworkError();

        void onSessionError();

        void onInvalidCourseError();
    }
}
