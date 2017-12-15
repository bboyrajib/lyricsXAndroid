package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ViewLyrics extends AppCompatActivity {

    TextView lyrics;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lyrics);

        Intent intent=getIntent();
        String song=intent.getStringExtra("clickSong");
        String artist=intent.getStringExtra("clickArtist");

        String[] arr=extract(song+" - "+artist,artist);

        lyrics=(TextView)findViewById(R.id.viewLyrics);

        String ticker=song+" - "+artist;
        getLyricsFunc(getUrl(arr[0],arr[1]),song,artist,ticker);




    }
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
        Log.i("SongString",string);
        String arr[] = {string, string1};
        string = "";
        string1 = "";
        return arr;
    }

    private String getUrl(String song, String artist) {
        String Url = "http://lyricsx.herokuapp.com/api/find/";


        //    Toast.makeText(this, Url, Toast.LENGTH_SHORT).show();
        Log.i("Url",Url+artist+"/"+song);
        return Url+artist+"/"+song;


    }

    private void getLyricsFunc(String URL, final String song, final String artist, final String ticker) {


        progressDialog=new ProgressDialog(ViewLyrics.this);
        progressDialog.setTitle("Fetching Lyrics");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                     progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    lyrics.setText(ticker.toUpperCase() + "\n\n" + lyric);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Toast.makeText(MainActivity.this, "Catch: 404", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ViewLyrics.this);
        queue.add(request);
    }
}
