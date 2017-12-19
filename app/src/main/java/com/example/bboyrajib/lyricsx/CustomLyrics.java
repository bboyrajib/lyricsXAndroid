package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
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

    private TextInputEditText song;
    private TextInputEditText artist;

    private TextView clyrics,getLyrics,getLyrics2;
    String song_name,artist_name;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_lyrics);
        song=(TextInputEditText) findViewById(R.id.song);
        artist=(TextInputEditText) findViewById(R.id.artist);
        getLyrics=(TextView) findViewById(R.id.getLyrics);
        getLyrics2=(TextView)findViewById(R.id.getLyrics2);
        //clyrics=(TextView)findViewById(R.id.customlyrics);

        song.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        artist.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
        getLyrics2.setTypeface(typeface);


        ActionBar actionBar=getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());
        tv.setText(actionBar.getTitle());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setTypeface(typeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        clyrics.setTypeface(typeface);
        song.setTypeface(typeface);
        artist.setTypeface(typeface);
        ((TextInputLayout)findViewById(R.id.tilsong)).setTypeface(typeface);
        ((TextInputLayout)findViewById(R.id.tilart)).setTypeface(typeface);
        getLyrics.setTypeface(typeface);

        getLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i("tag",artist_name+" ");
                 song_name=song.getText().toString();

                 artist_name=artist.getText().toString();

                if(song_name.isEmpty() ) {
                    song.setError("Please type the song name");
                    return;
                }
                if(artist_name==null || artist_name.isEmpty()) {
                    Log.i("tag","empty");
                    artist_name = " ";
                }





                Intent intent=new Intent(CustomLyrics.this,SearchResults.class);
                intent.putExtra("song",song_name);
                intent.putExtra("artist",artist_name);

              //  getLyricsFunc(getUrl(song_name,artist_name));
                song.setText(null);
                artist.setText(null);
                song.requestFocus();

                startActivity(intent);


            }
        });
        getLyrics2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                song_name=song.getText().toString();
                artist_name=artist.getText().toString();

                if(song_name.isEmpty() ) {
                    song.setError("Please type the song name");
                    return;
                }
                if(artist_name.isEmpty() || artist_name==null){
                    artist_name = " ";
                }


                Intent intent=new Intent(CustomLyrics.this,SearchResults.class);
                intent.putExtra("song",song_name);
                intent.putExtra("artist",artist_name);

                //  getLyricsFunc(getUrl(song_name,artist_name));
                song.setText(null);
                artist.setText(null);
                song.requestFocus();

                startActivity(intent);


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
                        clyrics.setText(song_name.toUpperCase()+" - "+artist_name.toUpperCase()+"\n\n"+"Sorry! No Lyrics found for this song");
                    clyrics.setText(song_name.toUpperCase()+" - "+artist_name.toUpperCase()+"\n\n"+lyric);
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
