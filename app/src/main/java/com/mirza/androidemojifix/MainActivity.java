package com.mirza.androidemojifix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;

import com.google.gson.GsonBuilder;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    private final String baseUrl = "http://emojipedia.org";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // thread to download emoji image files
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String rawHtml = request("http://emojipedia.org/apple/ios-9.1/", "", null, null, false);
                String emojiGrid = rawHtml.split("<ul class=\"emoji-grid\">")[1].split("</ul>")[0];
                String[] emojiList = emojiGrid.split("<a href=\"");
                for (int i = 1; i < emojiList.length; i++) {
                    String emojiUrl = baseUrl + emojiList[i].split("\">")[0];
                    String rawHtmlSpecificEmoji = request(emojiUrl, "", null, null, false);
                    String emojiName = rawHtmlSpecificEmoji.split("<input type=\"text\" value=\"")[1].split("\"")[0];
                    String imageUrl = emojiList[i].split("<img src=\"")[1].split("\"")[0];
                    saveImage(emojiName, imageUrl);
                }
            }
        });
        //thread.start();



        Intent i = new Intent(this, MainService.class);
        startService(i);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                        File.separator + "AndroidEmojiFix" + File.separator);
                File[] files = mediaDirectory.listFiles();
                Drawable[] drawables = new Drawable[files.length];
                for (int ij = 0; ij < files.length; ij++) {
                    drawables[ij] = Drawable.createFromPath(files[ij].getAbsolutePath());
                }

                File file = new File(getFilesDir(), "Emoji Drawables");
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(new GsonBuilder().create().toJson(drawables).getBytes());
                    Log.e("DATA", new GsonBuilder().create().toJson(drawables));
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //t.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void saveImage(final String emojiName, final String imageUrl) {
        byte[] bytes = requestBytes(imageUrl, "", null, null, false);
        try {
            File file = getOutputMediaFile(emojiName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String request(String baseUrl, String endPoint, Map<String, String> headers, JSONObject fields, boolean post) {
        try {
            HttpURLConnection httpsUrlConnection = (HttpURLConnection) new URL(baseUrl+endPoint).openConnection();

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpsUrlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (post) {
                httpsUrlConnection.setRequestMethod("POST");
                httpsUrlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(httpsUrlConnection.getOutputStream());
                wr.writeBytes(fields.toString());
                wr.flush();
                wr.close();
            }
            httpsUrlConnection.connect();
            int resultCode = httpsUrlConnection.getResponseCode();
            System.out.println("RESULT CODE= " + resultCode);
            for (Map.Entry<String, List<String>> header : httpsUrlConnection.getHeaderFields().entrySet()) {
                System.out.println(header.getKey() + "=" + header.getValue());
            }

            InputStream is = httpsUrlConnection.getInputStream();

            byte[] responseBytes = IOUtils.toByteArray(is);
            String response = new String(responseBytes, "UTF8");
            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] requestBytes(String baseUrl, String endPoint, Map<String, String> headers, JSONObject fields, boolean post) {
        try {
            HttpURLConnection httpsUrlConnection = (HttpURLConnection) new URL(baseUrl+endPoint).openConnection();

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpsUrlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (post) {
                httpsUrlConnection.setRequestMethod("POST");
                httpsUrlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(httpsUrlConnection.getOutputStream());
                wr.writeBytes(fields.toString());
                wr.flush();
                wr.close();
            }
            httpsUrlConnection.connect();
            int resultCode = httpsUrlConnection.getResponseCode();
            System.out.println("RESULT CODE= " + resultCode);
            for (Map.Entry<String, List<String>> header : httpsUrlConnection.getHeaderFields().entrySet()) {
                System.out.println(header.getKey() + "=" + header.getValue());
            }
            if (resultCode != 200) {
                return null;
            }

            InputStream is = httpsUrlConnection.getInputStream();

            byte[] responseBytes = IOUtils.toByteArray(is);
            return responseBytes;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getOutputMediaFile(String emoji) throws IOException {

        File mediaDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                File.separator + "AndroidEmojiFix" + File.separator);
        mediaDirectory.mkdirs();

        File mediaFile = new File(mediaDirectory, emoji+".png");

        return mediaFile;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
