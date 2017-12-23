package com.example.bboyrajib.lyricsx;

/**
 * Created by bboyrajib on 08/12/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NLService extends NotificationListenerService {

    Intent msgrcv;
    Context context;
    String pack,ticker,title,text,artist,song;
    //private NLServiceReceiver nlservicereciver;
    private boolean mInitialized;
    SharedPreferences prefs;

    //private NLServiceReceiver nlservicereciver;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


       Toast.makeText(NLService.this,"LyricsX is Active",Toast.LENGTH_SHORT).show();
        Log.d("yass","yass");

    }

    @Override
    public int onStartCommand(Intent intent,   int flags, int startId) {
        return START_STICKY;
    }

    /* @Override
    public IBinder onBind(Intent intent) {
        Log.i("Bind","Bind");
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("permission",true);
        editor.apply();
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("UnBind","UnBind");
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("permission",false);
        editor.apply();
        return super.onUnbind(intent);
    }*/

    @Override

    public void onNotificationPosted( StatusBarNotification sbn) {


         pack = sbn.getPackageName();

       // Log.d("ticker", ticker);
        Log.i("Package:",pack);
        if(pack.equals("com.spotify.music")) {

            if(sbn.getNotification().tickerText!=null)
                ticker = sbn.getNotification().tickerText.toString();
            Bundle extras = sbn.getNotification().extras;
            title = extras.getString("android.title");
            text = extras.getCharSequence("android.text").toString();

          //  sendNotification("LyricsX: "+ticker,"Tap to see lyrics");

            Log.d("Package", pack);
            Log.i("Ticker", ticker);
//            Log.i("Title",title);
            Log.i("Text", text);
            String arr[]=extract(ticker,text);
            song=arr[0];artist=arr[1];
            msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("song", arr[0]);
           // msgrcv.putExtra("title", title);
            msgrcv.putExtra("artist", arr[1]);
            if(!arr[0].isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("song", arr[0]);
                editor.putString("artist", arr[1]);
                editor.putString("ticker", ticker);
                editor.commit();
            }

            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            getLyricsFunc(getUrl(arr[0],arr[1]),arr[0],arr[1],ticker);
          //  new doIt().execute();

        }
        else if(pack.equals("com.google.android.music")){

            Bundle extras = sbn.getNotification().extras;
            title = extras.getString("android.title");
            text = extras.getCharSequence("android.text").toString();

            Log.i("Title",title);
            Log.i("Text", text);

            String arr[]=extract(title+" - "+text,text);
            song=arr[0];artist=arr[1];

            msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", title+" - "+text);
            msgrcv.putExtra("song", arr[0]);
            // msgrcv.putExtra("title", title);
            msgrcv.putExtra("artist", arr[1]);
            if(!arr[0].isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("song", arr[0]);
                editor.putString("artist", arr[1]);
                editor.putString("ticker", title + " - " + text);
                editor.commit();
            }

            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            getLyricsFunc(getUrl(arr[0],arr[1]),arr[0],arr[1],title + " - " + text);
          //  new doIt().execute();
        }



    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }

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

                        SpannableString ss1=  new SpannableString(ticker.toUpperCase());
                        ss1.setSpan(new RelativeSizeSpan(1.3f), 0,ticker.length(), 0);


                        //if(Utils.isAppIsInBackground(getApplicationContext())) Log.i("TAG","bg");
                            sendNotification();
                        SharedPreferences.Editor editor=prefs.edit();
                        editor.putString("lyrics",(TextUtils.concat(ss1,  "\n\n" , lyric).toString())).apply();
                       // Log.i("ticker",ticker+"");
                       // msgrcv.putExtra("lyrics",ticker.toUpperCase() + "\n\n" + lyric);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Toast.makeText(MainActivity.this, "Catch: 404", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
        RequestQueue queue = Volley.newRequestQueue(NLService.this);
        queue.add(request);
    }

    /*class NLServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

             if(intent.getStringExtra("command").equals("list")){


                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    pack = sbn.getPackageName();

                    // Log.d("ticker", ticker);
                    if(pack.equalsIgnoreCase("com.spotify.music")) {


                        ticker = sbn.getNotification().tickerText.toString();
                        Bundle extras = sbn.getNotification().extras;
                        title = extras.getString("android.title");
                        text = extras.getCharSequence("android.text").toString();

                        //  sendNotification("LyricsX: "+ticker,"Tap to see lyrics");

                        Log.d("Package", pack);
                        Log.i("Ticker", ticker);
                        //    Log.i("Title",title);
                        Log.i("Text", text);

                        Intent msgrcv = new Intent("Msg");
                        msgrcv.putExtra("package", pack);
                        msgrcv.putExtra("ticker", ticker);
                        msgrcv.putExtra("title", title);
                        msgrcv.putExtra("text", text);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                        break;
                    }
                }

            }
        }
    }*/

   /* private void sendNotification(String title,String body){

       // Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(1)

                .setLights(Color.RED, 3000, 3000)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //0);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        //updateMyActivity(this,body,title);

    }*/
   String string="",string1="";
   private String[] extract(String song, String artist) {
       for (int i = 0; i < song.length() - artist.length(); ++i) {

           if (Character.isLetter(song.charAt(i)) || Character.isDigit(song.charAt(i)) || song.charAt(i) == '&' || song.charAt(i) == '\'' || song.charAt(i) == ','  || song.charAt(i) == '(' || song.charAt(i) == ')')
               string += song.charAt(i);
           else if(song.charAt(i) == '?')
               string+="%3F";
           else if(song.charAt(i) == '#')
               continue;
           else
               string += "_";
       }
       for (int i = 0; i < artist.length(); ++i) {

           if (Character.isLetter(artist.charAt(i)) || Character.isDigit(artist.charAt(i)) || artist.charAt(i) == '&' || artist.charAt(i) == '\'' ||  artist.charAt(i) == ',' ||  artist.charAt(i) == '(' || artist.charAt(i) == ')')
               string1 += artist.charAt(i);
           else if(artist.charAt(i) == '?')
               string1+="%3F";

           else
               string1 += "_";
       }
       string=string.toLowerCase().replaceAll("remastered","_");
    ///   string=string.replaceAll(" live","_");
      // string=string.replaceAll("demo","_");
       string=string.replaceAll("reprise","_");
       string=string.replaceAll("unplugged","_");
       string=string.replaceAll("acoustic","_");
       Log.i("SongString",string);
       String arr[] = {string, string1.toLowerCase()};
       string = "";
       string1 = "";
       return arr;
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

            SpannableString ss1=  new SpannableString(tick.toUpperCase());
            ss1.setSpan(new RelativeSizeSpan(1.3f), 0,tick.length(), 0);

            if(words==null)
                return;

            else if(words.isEmpty()){
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("lyrics",TextUtils.concat("\n\n\n\n\n\n\n\n\n\n",ss1,"\n\nSorry! No Lyrics Found\n\nTry using Manual Search").toString());
                editor.apply();
               // msgrcv.putExtra("lyrics","\n\n\n\n\n\n\n\n\n\n"+ticker.toUpperCase()+"\n\nSorry! No Lyrics Found\n\nTry using Manual Search");
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lyrics",TextUtils.concat(ss1, "\n\n" , Html.fromHtml(words, Html.FROM_HTML_MODE_COMPACT)).toString()).apply();
               // msgrcv.putExtra("lyrics",ticker.toUpperCase() + "\n\n" + Html.fromHtml(words, Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("lyrics",TextUtils.concat(ss1, "\n\n" , Html.fromHtml(words)).toString()).apply();
               // msgrcv.putExtra("lyrics",ticker.toUpperCase() + "\n\n" + Html.fromHtml(words));
            }
            //if(Utils.isAppIsInBackground(NLService.this)) Log.i("TAG","bg");
                sendNotification();


        }
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
         notificationIntent.putExtra("lyrics",prefs.getString("lyrics",null));
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

}