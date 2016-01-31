package com.mirza.androidemojifix;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import de.greenrobot.event.EventBus;


/**
 * Created by CR7 on 1/31/2016.
 */
public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {
    private Drawable[] drawables;
    private String[] namesWithExtension;
    private Context context;

    public EmojiAdapter(Drawable[] drawables, String[] namesWithExtension, Context context) {
        this.drawables = drawables;
        this.namesWithExtension = namesWithExtension;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new EmojiView(parent.getContext(), 80);
        v.setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.anim.button_press));
        v.setPadding(10,10,10,10);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.button.setBackground(drawables[position]);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                EventBus.getDefault().post(new SendCharacterToBuffer(namesWithExtension[position].replace(".png", "").replace("️", "")));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawables.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EmojiView button;

        public ViewHolder(View itemView) {
            super(itemView);
            button = (EmojiView)itemView;
        }
    }
}
