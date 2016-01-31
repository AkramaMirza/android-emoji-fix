package com.mirza.androidemojifix;

import android.graphics.drawable.Drawable;

/**
 * Created by CR7 on 1/31/2016.
 */
public class GetDrawablesEvent {
    private Drawable[] drawables;
    private String[] namesWithExtension;
    public GetDrawablesEvent(Drawable[] drawables, String[] namesWithExtension) {
        this.drawables = drawables;
        this.namesWithExtension = namesWithExtension;
    }

    public Drawable[] getDrawables() {
        return drawables;
    }

    public String[] getNamesWithExtension() {
        return namesWithExtension;
    }
}
