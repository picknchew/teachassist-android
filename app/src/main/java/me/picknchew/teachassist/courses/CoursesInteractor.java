package me.picknchew.teachassist.courses;

import android.support.annotation.NonNull;

import java.util.List;

import me.picknchew.teachassist.TeachAssistApplication;
import me.picknchew.teachassistapi.TeachAssistService;
import me.picknchew.teachassistapi.model.CourseInfo;
import me.picknchew.teachassistapi.model.Session;
import me.picknchew.teachassistapi.responses.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesInteractor {
    private final TeachAssistService service = TeachAssistApplication.getInstance().getTeachAssistService();
    private final Listener listener;

    public CoursesInteractor(Listener listener) {
        this.listener = listener;
    }

    public void updateCourses(Session session) {
        service.getCoursesInfo(session).enqueue(new Callback<BaseResponse<List<CourseInfo>>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<CourseInfo>>> call, @NonNull Response<BaseResponse<List<CourseInfo>>> response) {
                BaseResponse<List<CourseInfo>> baseResponse = response.body();

                if (baseResponse == null || baseResponse.hasError()) {
                    listener.onSessionError();
                    return;
                }

                listener.onUpdate(baseResponse.get());
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<CourseInfo>>> call, @NonNull Throwable t) {
                listener.onNetworkError();
            }
        });
    }

    public interface Listener {
        void onUpdate(List<CourseInfo> courses);

        void onNetworkError();

        void onSessionError();
    }
}
