package com.example.bboyrajib.lyricsx;



import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    int ID;
    TextView lyrics;
    String pack,song,artist;
    String title = "", text = "", ticker = "";
    Bundle extras;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        lyrics = (TextView) findViewById(R.id.lyricsText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        if(prefs.getString("lyrics",null)!=null)
            lyrics.setText(prefs.getString("lyrics",null));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if(extras != null){
                    String data1 = extras.getString("song");
                    String data2 = extras.getString("artist");
                    String data3=extras.getString("ticker");
                    getLyricsFunc(getUrl(data1,data2),data1,data2,data3);
                    return;
                }*/
                if (song==null||ticker==null) {
                  //  getLyricsFunc(getUrl(prefs.getString("song",null),prefs.getString("artist",null)),prefs.getString("song",null),prefs.getString("artist",null),prefs.getString("ticker",null));
                    lyrics.setText(prefs.getString("lyrics",null));
                    return;
                }

                // lyrics.setText("Song: "+string+"\nArtist: "+string1);

                getLyricsFunc(getUrl(song,artist),song,artist,ticker);


            }
        });



        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomLyrics.class));
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.audio);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AudioRecognition.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();



        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
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
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        if (id == R.id.action_developers) {
            startActivity(new Intent(MainActivity.this,DeveloperActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            pack = intent.getStringExtra("package");
            song = intent.getStringExtra("song");
            artist = intent.getStringExtra("artist");
            ticker = intent.getStringExtra("ticker");

         //   if (!pack.equals("com.spotify.music") && !pack.equals("com.google.android.music"))
         //       return;
            // Toast.makeText(MainActivity.this,ticker,Toast.LENGTH_LONG).show();

           /* for(int i=0;i<ticker.length()-text.length();++i){
                char c = ticker.charAt(i);
                if(Character.isLetter(c))
                    string+=ticker.charAt(i);
                else
                    string+="_";
            }
            for(int i=0;i<text.length();++i){
                char c = text.charAt(i);
                if(Character.isLetter(c) )
                    string1+=text.charAt(i);
                else
                    string1+="_";
            }*/
            // lyrics.setText("Song: "+string+"\nArtist: "+string1);
            //getLyricsFunc(getUrl(string,string1));
            /*if(extras != null){
                String data1 = extras.getString("song");
                String data2 = extras.getString("artist");
                getLyricsFunc(getUrl(data1,data2));
                return;
            }*/
         //   Log.i("onRec:",""+prefs.getString("lyrics",null));
         //   if(prefs.getString("lyrics",null)!=null)
             //   lyrics.setText(intent.getStringExtra("lyrics"));





            getLyricsFunc(getUrl(song,artist),song,artist,ticker);




        }
    };


    private String getUrl(String song, String artist) {
        String Url = "http://lyricsx.herokuapp.com/api/find/";


      //    Toast.makeText(this, Url, Toast.LENGTH_SHORT).show();
        Log.i("Url",Url+artist+"/"+song);
        return Url+artist+"/"+song;


    }

    private void getLyricsFunc(String URL, final String song, final String artist, final String ticker) {


       /* progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Fetching");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    if (lyric.isEmpty()) {
                      //  String words;


                        new doIt().execute(ticker);




                    }
                    else {

                      //  sendNotification();

                        lyrics.setText(ticker.toUpperCase() + "\n\n" + lyric);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(MainActivity.this, "Catch: 404", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

  /*  private String[] extract(String song, String artist) {
        for (int i = 0; i < song.length() - text.length(); ++i) {

            if (Character.isLetter(song.charAt(i)) || Character.isDigit(song.charAt(i)) || song.charAt(i) == '&' || song.charAt(i) == '\'' || song.charAt(i) == ',' || song.charAt(i) == '?' || song.charAt(i) == '(' || song.charAt(i) == ')')
                string += song.charAt(i);
            else
                string += "_";
        }
        for (int i = 0; i < text.length(); ++i) {

            if (Character.isLetter(artist.charAt(i)) || Character.isDigit(artist.charAt(i)) || artist.charAt(i) == '&' || artist.charAt(i) == '\'' || artist.charAt(i) == '?' || artist.charAt(i) == '-' || artist.charAt(i) == ',' || artist.charAt(i) == '-' || artist.charAt(i) == '(' || artist.charAt(i) == ')')
                string1 += text.charAt(i);
            else
                string1 += "_";
        }
        string=string.toLowerCase().replaceAll("remastered","_");
        string=string.replaceAll("live","_");
        Log.i("SongString",string);
        String arr[] = {string, string1};
        return arr;
    }*/

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Log.i("yo","Task moved to back");
        moveTaskToBack(true);

       /* if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/
    }

   private void sendNotification() {

        // Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("LyricsX")
                .setContentText("Tap to view lyrics")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(1)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
      // notificationIntent.putExtra("song",song);
     //  notificationIntent.putExtra("artist",artist);
     //  notificationIntent.putExtra("tikcer",ticker);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //0);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());


        //updateMyActivity(this,body,title);
    }

    @Override
    protected void onDestroy() {
        Log.i("destroy","destroy");
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("song",null);
        editor.putString("artist",null);
        editor.putString("ticker",null);
        editor.apply();
        super.onDestroy();
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

    public class doIt extends AsyncTask<String,Void,Void> {

        String words,tick;
        @Override
        protected Void doInBackground(String... params) {

            tick=params[0];

            try {
                if(artist==null)
                    return null;
                Document document= Jsoup.connect("http://azlyrics.com/lyrics/"+remove_special(artist).toLowerCase()+"/"+remove_special(song).toLowerCase()+".html").get();
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

            if(words==null)
                return;

            else if(words.isEmpty()){
               lyrics.setText("\n\n\n\n\n\n\n\n\n\n"+tick.toUpperCase()+"\n\nSorry! No Lyrics Found\n\nTry using Manual Search");
                return;
            }
           // sendNotification();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                lyrics.setText(tick.toUpperCase()+"\n\n"+Html.fromHtml(words,Html.FROM_HTML_MODE_COMPACT));
            else
                lyrics.setText(tick.toUpperCase()+"\n\n"+Html.fromHtml(words));
        }
    }




}

