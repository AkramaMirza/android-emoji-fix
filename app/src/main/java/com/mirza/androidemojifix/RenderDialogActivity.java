package com.mirza.androidemojifix;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class RenderDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_render_dialog);

        getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));

        Button doneButton = (Button)findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RenderLayout renderLayout = (RenderLayout)findViewById(R.id.renderLayout);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        String textToRender = "";
        if (item.getText() != null) {
            textToRender = item.getText().toString();
        }
        ArrayList<String> chars = new ArrayList<>();
        try {
            byte[] textToRenderBytes = textToRender.getBytes("UTF-32BE");
            for (int i = 0; i + 3 < textToRenderBytes.length; i += 4) {
                byte[] subArray = {textToRenderBytes[i], textToRenderBytes[i+1], textToRenderBytes[i+2], textToRenderBytes[i+3]};
                String currChar = new String(subArray, "UTF-32BE");

                // if it is a messeddd up emoji (peace sign, heart, smiley face, finger pointing up)
                if (currChar.equals("☺") || currChar.equals("❤") || currChar.equals("✌") ||
                        currChar.equals("☝")) {

                    String nextChar = "️";
                    currChar = currChar + nextChar;
                }

                // if it is a modifier
                // TODO: check for country flags
                if (currChar.equals("🏻") || currChar.equals("🏼") || currChar.equals("🏽") || currChar.equals("🏾") ||
                        currChar.equals("🏿")) {
                    int index = chars.size() - 1;
                    if (chars.size() > 0) {
                        String lastChar = chars.get(index);
                        if (lastChar.equals("☝️") || lastChar.equals("☺️") || lastChar.equals("❤️")||
                                lastChar.equals("✌️")) {
                            lastChar = lastChar.split("️")[0];
                        }
                        String joinedChars = lastChar + currChar;
                        if (isEmoji(joinedChars)) {
                            chars.remove(index);
                            chars.add(joinedChars);
                        } else {
                            chars.add(currChar);
                        }
                    }
                // if it is a zero width joiner
                } else if (currChar.equals("\u200D")) {
                    int index = chars.size() - 1;
                    i += 4;
                    byte[] subArrayNext = {textToRenderBytes[i], textToRenderBytes[i+1], textToRenderBytes[i+2], textToRenderBytes[i+3]};
                    String nextChar = new String(subArrayNext, "UTF-32BE");
                    String joinedChars = chars.get(index) + currChar + nextChar;
                    chars.remove(index);
                    chars.add(joinedChars);
                } else {
                    chars.add(currChar);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        boolean textViewAvailable = false;
        for (int i = 0; i < chars.size(); i++) {
            if (isEmoji(chars.get(i))) {
                File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                        File.separator + "AndroidEmojiFix" + File.separator);
                File emojiFile = new File(mediaDirectory, chars.get(i)+".png");
                Bitmap myBitmap = BitmapFactory.decodeFile(emojiFile.getAbsolutePath());
                EmojiView emojiView = new EmojiView(this, 80);
                emojiView.setImageBitmap(myBitmap);
                renderLayout.addView(emojiView);
                textViewAvailable = false;
            } else if (chars.get(i).equals(" ")) {
                TextView textView = new TextView(this);
                textView.setText(" ");
                renderLayout.addView(textView);
                textViewAvailable = false;

            } else {
                if (textViewAvailable) {
                    TextView textView = (TextView)renderLayout.getChildAt(renderLayout.getChildCount() - 1);
                    textView.setText(textView.getText()+chars.get(i));
                } else {
                    TextView textView = new TextView(this);
                    textView.setTextSize(20);
                    textView.setText(chars.get(i));
                    renderLayout.addView(textView);
                    textViewAvailable = true;
                }
            }
        }


    }

    public boolean isEmoji(String currChar) {
        /*String raw = readFile(getFilesDir(), "Supported Emojis");
        JSONObject supportedEmojis = new JSONObject();
        try {
            supportedEmojis = new JSONObject(raw);
            if (supportedEmojis.get(currChar) != null) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                File.separator + "AndroidEmojiFix" + File.separator);
        File emojiFile = new File(mediaDirectory, currChar+".png");
        if (emojiFile.exists()) {
            return true;
        } else {
            return false;
        }
        /*String[] fileNames = mediaDirectory.list();
        for (int i = 0; i < fileNames.length; i++) {
            //Log.e("EMOJIS", fileNames[i]);
            if (fileNames[i].equals(currChar+".png")) {
                return true;
            }
        }
        return false;*/
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
        getMenuInflater().inflate(R.menu.menu_render_dialog, menu);
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
