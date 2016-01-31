package com.mirza.androidemojifix;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by CR7 on 1/30/2016.
 */
public class RenderLayout extends ViewGroup {
    private int deviceWidth;
    private int emojiSize;

    public RenderLayout(Context context, int emojiSize) {
        super(context);
        init(context);
        this.emojiSize = emojiSize;
    }

    public RenderLayout(Context context) {
        super(context);
    }

    public RenderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RenderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
