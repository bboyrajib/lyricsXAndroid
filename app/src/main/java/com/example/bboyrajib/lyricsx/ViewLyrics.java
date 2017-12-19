package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewLyrics extends AppCompatActivity {

    TextView lyrics;
    ProgressDialog progressDialog;

    String artist,song;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lyrics);

        Intent intent=getIntent();
         song=intent.getStringExtra("clickSong");
         artist=intent.getStringExtra("clickArtist");

        String[] arr=extract(song+" - "+artist,artist);

        lyrics=(TextView)findViewById(R.id.viewLyrics);
        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");

        ActionBar actionBar=getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());
        tv.setText(actionBar.getTitle());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setTypeface(typeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);




        lyrics.setTypeface(typeface);
        progressBar = (ProgressBar) findViewById(R.id.progressbarVL);


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
        string=string.replaceAll("acoustic","_");
        Log.i("SongString",string);
        String arr[] = {string, string1.toLowerCase()};
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


      /*  progressDialog=new ProgressDialog(ViewLyrics.this);
        progressDialog.setTitle("Fetching Lyrics");
        progressDialog.setMessage("Please wait a moment");
        progressDialog.setCancelable(true);
        progressDialog.show();*/
        progressBar.setVisibility(View.VISIBLE);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    if(lyric.isEmpty()){
                        new doIt().execute();
                    }
                    else {
                       // progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                        lyrics.setText(ticker.toUpperCase() + "\n\n" + lyric);
                    }
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

            String ticker=song+" - "+artist;
           // progressDialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);

            if(words==null || words.isEmpty()){
                lyrics.setText("\n\n\n\n\n\n\n\n\n\n\n"+ticker.toUpperCase()+"\n\nSorry! No Lyrics found for this song");
            }


           // sendNotification();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                lyrics.setText(ticker.toUpperCase()+"\n\n"+ Html.fromHtml(words,Html.FROM_HTML_MODE_COMPACT));
            else
                lyrics.setText(ticker.toUpperCase()+"\n\n"+Html.fromHtml(words));
        }
    }
}
