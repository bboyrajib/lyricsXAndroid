package com.example.bboyrajib.lyricsx;

/**
 * Created by bboyrajib on 08/12/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.LinkedList;


public class NLService extends NotificationListenerService {

    Context context;
    String pack,ticker,title,text;
    //private NLServiceReceiver nlservicereciver;
    private boolean mInitialized;
    SharedPreferences prefs;

    //private NLServiceReceiver nlservicereciver;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


       Toast.makeText(NLService.this,"LyricsX is Active",Toast.LENGTH_LONG).show();
        Log.d("yass","yass");

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

            Intent msgrcv = new Intent("Msg");
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

        }
        else if(pack.equals("com.google.android.music")){

            Bundle extras = sbn.getNotification().extras;
            title = extras.getString("android.title");
            text = extras.getCharSequence("android.text").toString();

            Log.i("Title",title);
            Log.i("Text", text);

            String arr[]=extract(title+" - "+text,text);

            Intent msgrcv = new Intent("Msg");
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
        }

    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

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

}