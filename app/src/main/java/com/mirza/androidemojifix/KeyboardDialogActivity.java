package com.mirza.androidemojifix;

import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.greenrobot.event.EventBus;


public class KeyboardDialogActivity extends Activity {
    private EditText emojiEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_keyboard_dialog);
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));

        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new RequestDrawablesEvent());

        final BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(KeyboardDialogActivity.this).inflate(R.layout.bubble_layout, null);
        bubbleLayout.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubbleLayout) {
                EventBus.getDefault().post(new RemoveBubbleEvent(bubbleLayout));
                Intent i = new Intent(getApplicationContext(), KeyboardDialogActivity.class);
                startActivity(i);
            }
        });

        emojiEditText = (EditText)findViewById(R.id.emojiEditText);

        Button doneButton = (Button)findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToClipboard();
                EventBus.getDefault().post(new AddBubbleEvent(bubbleLayout));
                finish();
            }
        });
    }

    public void addToClipboard() {
        String input = emojiEditText.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", input);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard: " +input, Toast.LENGTH_LONG).show();
    }

    public void onEvent(SendCharacterToBuffer e) {
        emojiEditText.setText(emojiEditText.getText().toString() + e.getInput());
        emojiEditText.setSelection(emojiEditText.getText().length());
    }

    public void onEvent(GetDrawablesEvent e) {
        Drawable[] drawables = e.getDrawables();
        String[] namesWithExtension = e.getNamesWithExtension();
        EmojiAdapter emojiAdapter = new EmojiAdapter(drawables, namesWithExtension, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.emojiRecyclerView);
        recyclerView.setAdapter(emojiAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = (int) (KeyboardDialogActivity.this.getResources().getDisplayMetrics().density * 10);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
    }

    public String readFile(File dir, String fileName) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keyboard_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
