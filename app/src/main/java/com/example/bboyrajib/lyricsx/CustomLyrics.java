package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class CustomLyrics extends AppCompatActivity {

    private EditText song;
    private EditText artist;
    private Button getLyrics;
    private TextView clyrics;
    String song_name,artist_name;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_lyrics);
        song=(EditText)findViewById(R.id.song);
        artist=(EditText)findViewById(R.id.artist);
        getLyrics=(Button)findViewById(R.id.getLyrics);
        clyrics=(TextView)findViewById(R.id.customlyrics);

        getLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 song_name=song.getText().toString();
                 artist_name=artist.getText().toString();

                if(song_name.isEmpty() || artist_name.isEmpty())
                    return;
                try  {
                    Log.i("tag","inside try");
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getLyricsFunc(getUrl(song_name,artist_name));
                song.setText(null);
                artist.setText(null);
                song.requestFocus();


            }
        });
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
        string=string.replaceAll("acoustic","_");
        string=string.replaceAll("unplugged","_");
        Log.i("SongString", string);
        String arr[] = {string, string1.toLowerCase()};
        string = "";
        string1 = "";
        return arr;
    }
    private String getUrl(String song,String artist){
        String Url = "http://lyricsx.herokuapp.com/api/find/";
        Log.i("Url",Url);

        String arr[]=extract(song+" - "+artist,artist);

        Url+=arr[1]+"/"+arr[0];
        Log.i("Url",Url);

        return Url;
    }

     private void getLyricsFunc(String URL){

        progressDialog=new ProgressDialog(CustomLyrics.this);
        progressDialog.setTitle("Fetching Lyrics");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCancelable(true);
        progressDialog.show();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    progressDialog.dismiss();
                    JSONObject jsonObject=new JSONObject(response);
                    String lyric= jsonObject.getString("lyric");
                    if(lyric.isEmpty())
                        clyrics.setText(song_name.toUpperCase()+" - "+artist_name.toUpperCase()+"\n----------------------------------------------\n\n"+"Sorry! No Lyrics found for this song");
                    clyrics.setText(song_name.toUpperCase()+" - "+artist_name.toUpperCase()+"\n----------------------------------------------\n\n"+lyric);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CustomLyrics.this,"Catch: 404",Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
         RequestQueue queue = Volley.newRequestQueue(CustomLyrics.this);
         queue.add(request);
    }

}
