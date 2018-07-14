package me.picknchew.teachassist.courses;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class CourseInfoItemDecoration extends RecyclerView.ItemDecoration {
    // left and right margin
    private final static int HORIZONTAL_MARGIN_DP = 21;
    private final static int BOTTOM_MARGIN_DP = 25;
    private final int horizontalMargin;
    private final int bottomMargin;


    public CourseInfoItemDecoration(@NonNull Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HORIZONTAL_MARGIN_DP, metrics);
        bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BOTTOM_MARGIN_DP, metrics);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = horizontalMargin;
        outRect.left = horizontalMargin;
        outRect.bottom = bottomMargin;
    }
}
