package com.example.bboyrajib.lyricsx;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecognition extends AppCompatActivity implements IACRCloudListener {
    //NOTE: You can also implement IACRCloudResultWithAudioListener, replace "onResult(String result)" with "onResult(ACRCloudResult result)"

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;

    private TextView mVolume, mResult, tv_time;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    String title,artist;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recognition);

      /*  path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud/model";

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }*/


        mResult = (TextView) findViewById(R.id.recognize);


        Button startBtn = (Button) findViewById(R.id.start);



        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                start();
                Toast.makeText(AudioRecognition.this,"Listening!",Toast.LENGTH_LONG).show();
            }
        });




        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        this.mConfig.context = this;
        this.mConfig.host = "identify-ap-southeast-1.acrcloud.com";
     //   this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "80923b0b243154b69f91f05dcabb7d0e";
        this.mConfig.accessSecret = "gjptvT4GQSoTDLvOIF5ZbvZ4gp8PzXHi9jZveLoK";
        this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        Log.i("init",""+this.initState);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }

    }


    public void start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
//            mVolume.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                mResult.setText("start error!");
            }
            startTime = System.currentTimeMillis();
        }
    }

  /*  protected void stop() {
        if (mProcessing && this.mClient != null) {
            this.mClient.stopRecordToRecognize();
        }
        mProcessing = false;

        stopTime = System.currentTimeMillis();
    }

    protected void cancel() {
        if (mProcessing && this.mClient != null) {
            mProcessing = false;
            this.mClient.cancel();
            tv_time.setText("");
            mResult.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    // Old api
    @Override
    public void onResult(String result) {
        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }



        try {

            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                //
                /*if (metadata.has("humming")) {
                    JSONArray hummings = metadata.getJSONArray("humming");
                    for(int i=0; i<hummings.length(); i++) {
                        JSONObject tt = (JSONObject) hummings.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");
                        tres = tres + (i+1) + ".  " + title + "\n";
                    }
                }*/
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        artist = art.getString("name");

                    }
                    getLyricsFunc(getUrl(title,artist),title,artist,title+" - "+artist);
                }
             /*  if (metadata.has("streams")) {
                    JSONArray musics = metadata.getJSONArray("streams");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        String channelId = tt.getString("channel_id");
                        tres = tres + (i+1) + ".  Title: " + title + "    Channel Id: " + channelId + "\n";
                    }
                }
                if (metadata.has("custom_files")) {
                    JSONArray musics = metadata.getJSONArray("custom_files");
                    for(int i=0; i<musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        tres = tres + (i+1) + ".  Title: " + title + "\n";
                    }
                }*/

            }

        } catch (JSONException e) {
            Toast.makeText(AudioRecognition.this,"Sorry! Music not found",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onVolumeChanged(double volume) {

    }
    private String getUrl(String song, String artist) {
        String Url = "http://lyricsx.herokuapp.com/api/find/";

        String arr[]=extract(song+" - "+artist,artist);
        //    Toast.makeText(this, Url, Toast.LENGTH_SHORT).show();
        Log.i("Url",Url+artist+"/"+song);
        return Url+arr[1]+"/"+arr[0];


    }

    private void getLyricsFunc(String URL, final String song, final String artist, final String ticker) {


        progressDialog=new ProgressDialog(AudioRecognition.this);
        progressDialog.setTitle("Lyrics Found : Fetching");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    if (lyric.isEmpty()) {
                        mResult.setText("\n\n\n\n\n\n\n\n\n\n"+ticker.toUpperCase()+"\nSorry! No Lyrics Found\nTry using Manual Search");
                        return;
                    }

                    //  sendNotification(song,artist,ticker);

                    mResult.setText(ticker.toUpperCase() + "\n\n" + lyric);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AudioRecognition.this, "Catch: 404", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
        queue.add(request);
    }

    String string="",string1="";
    private String[] extract(String song, String artist) {
        for (int i = 0; i < song.length() - artist.length(); ++i) {

            if (Character.isLetter(song.charAt(i)) || Character.isDigit(song.charAt(i)) || song.charAt(i) == '&' || song.charAt(i) == '\'' || song.charAt(i) == ',' || song.charAt(i) == '(' || song.charAt(i) == ')')
                string += song.charAt(i);
            else if (song.charAt(i) == '?')
                string += "%3F";
            else
                string += "_";
        }
        for (int i = 0; i < artist.length(); ++i) {

            if (Character.isLetter(artist.charAt(i)) || Character.isDigit(artist.charAt(i)) || artist.charAt(i) == '&' || artist.charAt(i) == '\'' || artist.charAt(i) == ',' || artist.charAt(i) == '(' || artist.charAt(i) == ')')
                string1 += artist.charAt(i);
            else if (artist.charAt(i) == '?')
                string1 += "%3F";
            else
                string1 += "_";
        }
        string = string.toLowerCase().replaceAll("remastered", "_");
        string = string.replaceAll("live", "_");
        Log.i("SongString", string);
        String arr[] = {string, string1};
        string = "";
        string1 = "";
        return arr;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("AudioRecognition", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(AudioRecognition.this,MainActivity.class));
        AudioRecognition.this.finish();
    }
}

