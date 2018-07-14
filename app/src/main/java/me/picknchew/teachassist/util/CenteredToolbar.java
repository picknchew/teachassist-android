package me.picknchew.teachassist.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import me.picknchew.teachassist.R;

public class CenteredToolbar extends Toolbar {

    private TextView centeredTitleTextView;

    public CenteredToolbar(Context context) {
        super(context);
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        setTitle(getResources().getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        getCenteredTitleTextView().setText(title);
    }

    @Override
    public CharSequence getTitle() {
        return getCenteredTitleTextView().getText().toString();
    }

    public void setTypeface(Typeface font) {
        getCenteredTitleTextView().setTypeface(font);
    }

    private TextView getCenteredTitleTextView() {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = new TextView(getContext());
            centeredTitleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.pt_sans_bold));
            centeredTitleTextView.setSingleLine();
            centeredTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            centeredTitleTextView.setAllCaps(true);
            centeredTitleTextView.setTextSize(18F);
            centeredTitleTextView.setGravity(Gravity.CENTER);
            centeredTitleTextView.setTextColor(getResources().getColor(R.color.colorWhite));

            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            centeredTitleTextView.setLayoutParams(lp);

            addView(centeredTitleTextView);
        }

        return centeredTitleTextView;
    }
}