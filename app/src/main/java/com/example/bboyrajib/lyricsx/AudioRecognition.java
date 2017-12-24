package com.example.bboyrajib.lyricsx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;
import com.squareup.picasso.Picasso;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecognition extends AppCompatActivity implements IACRCloudListener {
    //NOTE: You can also implement IACRCloudResultWithAudioListener, replace "onResult(String result)" with "onResult(ACRCloudResult result)"

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;
    int flag=0;
    private TextView mVolume, mResult, tv_time;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "",host,accessKey,accessSecret;

    private long startTime = 0;
    private long stopTime = 0;

    Menu menu;

    String timestamp;
    Timestamp ts;
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    String title,artist,album,trackID,has_lyric="0",imageURL;
    RelativeLayout relativeLayout;
    String FETCH_API="http://bboyrajibx.xyz/lyricsxapi.php";
    String createTableURL="http://bboyrajibx.xyz/lyricsx_create_table.php";
    String backupToURL="http://bboyrajibx.xyz/lyricsx_backup_to_table.php";
    String restoreFromURL="http://bboyrajibx.xyz/lyricsx_restore_from_table.php";
  //  Set<String> set;
    //Set<String> fetch;

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Button startBtn,listbtn;

    SeekBar seekBar;
    Button save;


    SharedPreferences prefs;
    CardView cardView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recognition);
        progressBar=(ProgressBar)findViewById(R.id.progressbarAR);



      /*  path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud/model";

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }*/
      //  set = new HashSet<String>();
        relativeLayout=(RelativeLayout)findViewById(R.id.rel);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mResult = (TextView) findViewById(R.id.recognize);
        cardView=(CardView)findViewById(R.id.cv);

        imageView=(ImageView)findViewById(R.id.cardImage);

        if(prefs.getBoolean("isNightModeEnabledTrue", false)){
            cardView.setBackgroundColor(Color.parseColor("#29282e"));
            mResult.setTextColor(Color.WHITE);
        }


        seekBar=(SeekBar)findViewById(R.id.seekBar);
        imageView.setImageAlpha(seekBar.getProgress());
        save=(Button)findViewById(R.id.saveAR);



        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
        mResult.setTypeface(typeface);
        save.setTypeface(typeface);

        seekBar.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        ActionBar actionBar=getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());
        tv.setText(actionBar.getTitle());
        tv.setTextColor(Color.parseColor("#fcfcfc"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setTypeface(typeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



         startBtn = (Button) findViewById(R.id.start);
       //  listbtn = (Button) findViewById(R.id.list);

        if(!prefs.getBoolean("isAccountSelected",false)) {
            startBtn.setVisibility(View.GONE);

            mResult.setText("\n\n\n\n\n\n\n\n\n\n\nPlease select an account to continue!");
            pickUserAccount();
        }

      //
        else {
            progressBar.setVisibility(View.VISIBLE);
            imageView.setImageResource(0);
         //   Toast.makeText(this, "Initializing!", Toast.LENGTH_SHORT).show();


            this.mConfig = new ACRCloudConfig();
            this.mConfig.acrcloudListener = this;
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean flag = jsonObject.getBoolean("success");
                        if (flag) {
                            progressBar.setVisibility(View.INVISIBLE);
                           // Toast.makeText(AudioRecognition.this, "Initialized!", Toast.LENGTH_SHORT).show();
                            Log.i("Success", "Success");
                            host = jsonObject.getString("host");
                            accessKey = jsonObject.getString("access_key");
                            accessSecret = jsonObject.getString("access_secret");
                            Log.i("Success", host);
                            Log.i("Success", accessKey);
                            Log.i("Success", accessSecret);
                            mConfig.context = AudioRecognition.this;
                            mConfig.host = host;//"identify-ap-southeast-1.acrcloud.com";
                            //   this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
                            mConfig.accessKey = accessKey;//"80923b0b243154b69f91f05dcabb7d0e";
                            Log.i("host", " " + host);
                            mConfig.accessSecret = accessSecret; //"gjptvT4GQSoTDLvOIF5ZbvZ4gp8PzXHi9jZveLoK";
                            mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
                            mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
                            //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
                            //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

                            mClient = new ACRCloudClient();
                            // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
                            // the function initWithConfig is used to load offline db, and it may cost long time.
                            initState = mClient.initWithConfig(mConfig);
                            Log.i("init", "" + initState);
                            if (initState) {
                                mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
                            }


                        }

                    } catch (Exception e) {
                        Toast.makeText(AudioRecognition.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            };
            LyricsRequest request = new LyricsRequest(FETCH_API, responseListener);
            request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
            queue.add(request);
        }


       /* this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        this.mConfig.context = this;
        this.mConfig.host = host;//"identify-ap-southeast-1.acrcloud.com";
     //   this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey =accessKey;//"80923b0b243154b69f91f05dcabb7d0e";
        Log.i("host"," "+host);
        this.mConfig.accessSecret =accessSecret; //"gjptvT4GQSoTDLvOIF5ZbvZ4gp8PzXHi9jZveLoK";
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
        }*/
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                menu.getItem(0).setVisible(false);
                menu.getItem(0).setEnabled(false);
                if(flag==0) {
                    if (start()) {
                        progressBar.setVisibility(View.VISIBLE);
                        imageView.setImageResource(0);
                        imageURL = "";
                        //  Toast.makeText(AudioRecognition.this, "Listening!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    stop();
                    cancel();
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(0);
                    imageURL = "";

                }
            }
        });




        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                        imageView.setImageAlpha(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        imageView.setImageAlpha(progress);
                        Log.i("progress"," "+progress);
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                startBtn.setVisibility(View.VISIBLE);

            }
        });



    }


    public boolean start() {

        if (!this.initState) {
            Toast.makeText(this, "Initializing! Please wait!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mProcessing) {
            flag=1;
            mProcessing = true;
//            mVolume.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                mResult.setText("start error!");
            }
            startTime = System.currentTimeMillis();
        }
        return true;
    }

    protected void stop() {
        flag=0;
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

   /* @Override
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
            flag=0;
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");

                date=new Date(System.currentTimeMillis());

                timestamp=sdf.format(date);
                timestamp=timestamp.substring(0,timestamp.indexOf("."));
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
                        JSONObject albumObject=tt.getJSONObject("album");
                        album=albumObject.getString("name");
                        Log.i("album"," "+album);
                        if(tt.has("external_metadata")) {
                            JSONObject externalmetadataObject = tt.getJSONObject("external_metadata");
                            if(externalmetadataObject.has("spotify")) {
                                JSONObject spotifyObject = externalmetadataObject.getJSONObject("spotify");
                                JSONObject trackObject = spotifyObject.getJSONObject("track");
                                trackID = trackObject.getString("id");
                                getImageUrl(trackID);
                            }
                            else {
                                trackID = "NOID";
                                imageURL="https://deathgrind.club/uploads/posts/2017-09/1506510157_no_cover.png";
                            }
                        }
                        Log.i("ID",trackID);

                    }


                  //  prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    /*SharedPreferences.Editor editor = prefs.edit();
                        if(prefs.getString("listSong",null)==null){
                            //editor.putString("list", title + " - " + artist);
                            editor.putString("listSong",title);
                            editor.putString("listArtist",artist);
                            editor.putString("listAlbum",album);
                            editor.putString("trackID",trackID);


                            editor.commit();

                        }
                        else {
                          //  editor.putString("list", title + " - " + artist + "\n\n" + prefs.getString("list", null));
                            editor.putString("listSong", title  + "\n\n" + prefs.getString("listSong", null));
                            editor.putString("listArtist", artist + "\n\n" + prefs.getString("listArtist", null));
                            editor.putString("listAlbum", album + "\n\n" + prefs.getString("listAlbum", null));
                            editor.putString("trackID", trackID + "\n\n" + prefs.getString("trackID", null));
                            editor.commit();
                        }*/
                    progressBar.setVisibility(View.INVISIBLE);
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
             mResult.setTextIsSelectable(true);

            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                mResult.setText(mResult.getText()+"\n\n"+ "Sorry! Music not found in database");
                //Toast.makeText(AudioRecognition.this, "Sorry! Music not found in Database", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(AudioRecognition.this,"Sorry! Music not found",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onVolumeChanged(double volume) {

        long time = (System.currentTimeMillis() - startTime) / 1000;
        mResult.setText("\n\n\n\n\n\n\n\nPress Button to start listening..\n\nYour lyrics will appear here\n\nTime Elapsed: "+time + " s");

    }
    private String getUrl(String song, String artist) {
        String Url = "http://lyricsx.herokuapp.com/api/find/";

        String arr[]=extract(song+" - "+artist,artist);
        //    Toast.makeText(this, Url, Toast.LENGTH_SHORT).show();
        Log.i("Url",Url+artist+"/"+song);
        return Url+arr[1]+"/"+arr[0];


    }

    private void getLyricsFunc(String URL, final String song, final String artist, final String ticker) {

        final Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
      //  mytv.setText("Song detected : Fetching Lyrics");
      //  mytv.setTypeface(typeface);
        progressDialog=new ProgressDialog(AudioRecognition.this);
        progressDialog.setTitle(Utils.typeface(typeface,"Song detected : Fetching Lyrics"));
        progressDialog.setMessage(Utils.typeface(typeface,ticker));
        progressDialog.setCancelable(true);
        progressDialog.show();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    if (lyric.isEmpty()) {
                       // mResult.setText("\n\n\n\n\n\n\n\n\n\n"+ticker.toUpperCase()+"\n\nSorry! No Lyrics Found\n\nTry using Manual Search");
                       // has_lyric="0";
                        new doIt().execute();

                        return;
                    }
                    has_lyric="1";
                    backUpMyData();

                    progressDialog.dismiss();
                    SpannableString ss1=  new SpannableString(ticker.toUpperCase());
                    ss1.setSpan(new RelativeSizeSpan(1.3f), 0,ticker.length(), 0);


                    //  sendNotification(song,artist,ticker);

                    mResult.setText(TextUtils.concat(ss1,  "\n\n" , lyric));
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

            if (Character.isLetter(artist.charAt(i))  || Character.isDigit(artist.charAt(i)) || artist.charAt(i) == '&' || artist.charAt(i) == '\'' || artist.charAt(i) == ',' || artist.charAt(i) == '(' || artist.charAt(i) == ')')
                string1 += artist.charAt(i);
            else if (artist.charAt(i) == '?')
                string1 += "%3F";
            else
                string1 += "_";
        }
        string = string.toLowerCase().replaceAll("remastered", "_");
      //  string = string.replaceAll("\\slive", "_");
        string=string.replaceAll("acoustic","_");
        Log.i("SongString", string);
        String arr[] = {string, string1.toLowerCase()};
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


    public void pickUserAccount() {
    /*This will list all available accounts on device without any filtering*/
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, 23);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 23) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                startBtn.setVisibility(View.VISIBLE);
                listbtn.setVisibility(View.VISIBLE);
                mResult.setText("\n\n\n\n\n\n\n\n\n\n\nPress Button to start listening..\n\nYour lyrics will appear here");
                initialise();
              //  Toast.makeText(this, "Initializing!", Toast.LENGTH_SHORT).show();
                String username;
                username = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                username = username.substring(0, username.indexOf("@")).replace(".", "");
                //   Toast.makeText(this,username,Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isAccountSelected", true);
                editor.putString("username", username);
                editor.apply();
            } else if (resultCode == RESULT_CANCELED) {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Please select an account to continue!", Snackbar.LENGTH_INDEFINITE).setAction("SELECT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickUserAccount();
                    }
                });
                snackbar.setActionTextColor(Color.rgb(71, 145, 148));
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }
        }
    }

    private void backUpMyData(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // progressDialog.dismiss();
                    backupData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        String user=prefs.getString("username",null);
        BackupRestorePostRequest request = new BackupRestorePostRequest(createTableURL, user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
        queue.add(request);
    }


    private void backupData(){

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("Backup",new JSONObject(response).getString("success")+" ");

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };
        Log.i("All",title+" "+album+" "+artist+" "+trackID+" "+has_lyric+" "+imageURL);
      //  Log.i("requests to server"," " +songs[i]);
        BackupRestorePostRequest request = new BackupRestorePostRequest(backupToURL, prefs.getString("username",null), title,artist,album,trackID,has_lyric,imageURL,timestamp, listener);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
        queue.add(request);
    }

    private void initialise(){
        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean flag= jsonObject.getBoolean("success");
                    if(flag){
                        progressBar.setVisibility(View.GONE);
                       // Toast.makeText(AudioRecognition.this, "Initialized!", Toast.LENGTH_SHORT).show();
                        Log.i("Success","Success");
                        host=jsonObject.getString("host");
                        accessKey=jsonObject.getString("access_key");
                        accessSecret=jsonObject.getString("access_secret");
                        Log.i("Success",host);
                        Log.i("Success",accessKey);
                        Log.i("Success",accessSecret);
                        mConfig.context = AudioRecognition.this;
                        mConfig.host = host;//"identify-ap-southeast-1.acrcloud.com";
                        //   this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
                        mConfig.accessKey =accessKey;//"80923b0b243154b69f91f05dcabb7d0e";
                        Log.i("host"," "+host);
                        mConfig.accessSecret =accessSecret; //"gjptvT4GQSoTDLvOIF5ZbvZ4gp8PzXHi9jZveLoK";
                        mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
                        mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
                        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
                        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

                        mClient = new ACRCloudClient();
                        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
                        // the function initWithConfig is used to load offline db, and it may cost long time.
                        initState = mClient.initWithConfig(mConfig);
                        Log.i("init",""+initState);
                        if (initState) {
                            mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
                        }


                    }

                }catch (Exception e){
                    Toast.makeText(AudioRecognition.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };
        LyricsRequest request = new LyricsRequest("http://eurus.000webhostapp.com/lyricsxapi.php", responseListener);
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
        queue.add(request);
    }

    private void getImageUrl(String ID){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    menu.getItem(0).setVisible(true);
                    menu.getItem(0).setEnabled(true);
                    JSONObject jsonObject=new JSONObject(response);
                    Log.i("imgres",""+jsonObject.getString("success"));
                    if(jsonObject.getString("success")!=null || !jsonObject.getString("success").isEmpty()) {
                        imageURL = jsonObject.getString("success");
                        imageURL = imageURL.replace("\\", "");
                        Picasso.with(AudioRecognition.this).load(imageURL).into(imageView);
                    }
                    else
                        imageURL="https://deathgrind.club/uploads/posts/2017-09/1506510157_no_cover.png";

                  //  new doImg().execute();
                    backUpMyData();


                }catch (Exception e){
                    Toast.makeText(AudioRecognition.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };
        LyricsRequest request = new LyricsRequest("http://bboyrajibx.xyz/spotify/finder.php?keyword="+ID, responseListener);
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(AudioRecognition.this);
        queue.add(request);


    }

    public String remove_special(String c){

        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(c);
        while(match.find())
        {
            String s= match.group();
            c=c.replaceAll("\\"+s, "");
        }
        return c;
    }

    public class doIt extends AsyncTask<Void,Void,Void> {

        String words;
        @Override
        protected Void doInBackground(Void... params) {



            try {
                if(artist==null)
                    return null;
                Document document= Jsoup.connect("http://azlyrics.com/lyrics/"+remove_special(artist).toLowerCase()+"/"+remove_special(title).toLowerCase()+".html").get();
                Elements divs=document.select("div").not("[class]");
                // Log.i("Tag",divs.get(1).html().toString()+" ");


                words=divs.get(1).html().toString();

            } catch (IOException e) {
                e.printStackTrace();
                words="";
            }


            return null;
        }

        @Override
        @SuppressWarnings("deprecation")
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String ticker=title+" - "+artist;
            progressDialog.dismiss();


            SpannableString ss1=  new SpannableString(ticker.toUpperCase());
            ss1.setSpan(new RelativeSizeSpan(1.3f), 0,ticker.length(), 0);


            if(words==null) {
                has_lyric="0";
                return;
            }

            else if(words.isEmpty()){
                mResult.setText(TextUtils.concat("\n\n\n\n\n\n\n\n",ss1,"\n\nSorry! No Lyrics Found\n\nTry using Manual Search"));
                return;
            }
            // sendNotification();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                mResult.setText(TextUtils.concat(ss1,"\n\n",Html.fromHtml(words,Html.FROM_HTML_MODE_COMPACT)));
            else
                mResult.setText(TextUtils.concat(ss1,"\n\n",Html.fromHtml(words)));

            has_lyric="1";

            backUpMyData();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_recog, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(0).setEnabled(false);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            seekBar.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.GONE);
        }
        if(id == R.id.listAR){
            startActivity(new Intent(AudioRecognition.this, SongListRecyclerView.class));
        }


        return super.onOptionsItemSelected(item);
    }


}


