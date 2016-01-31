package com.mirza.androidemojifix;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by CR7 on 1/30/2016.
 */
public class EmojiView extends ImageView {
    private int emojiSize;

    public EmojiView(Context context, int emojiSize) {
        super(context);
        this.emojiSize = emojiSize;
    }

    public EmojiView(Context context) {
        super(context);
    }

    public EmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(emojiSize, emojiSize);
    }
}
