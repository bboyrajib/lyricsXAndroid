package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewLyrics extends AppCompatActivity {

    TextView lyrics;
    ProgressDialog progressDialog;

    String artist,song,imageURL;
    ProgressBar progressBar;
    ImageView imageView;
    SeekBar seekBar;
    Button save;
    CardView cardView;
    SharedPreferences prefs;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lyrics);

        imageView=(ImageView)findViewById(R.id.cardImageVL);
        prefs= PreferenceManager.getDefaultSharedPreferences(ViewLyrics.this);
        cardView=(CardView)findViewById(R.id.cvVL);


        Intent intent=getIntent();
         song=intent.getStringExtra("clickSong");
         artist=intent.getStringExtra("clickArtist");
         imageURL=intent.getStringExtra("imageURL");

        String[] arr=extract(song+" - "+artist,artist);

        lyrics=(TextView)findViewById(R.id.viewLyrics);
        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");

        ActionBar actionBar=getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());
        tv.setText(actionBar.getTitle());
        tv.setTextColor(Color.parseColor("#fcfcfc"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setTypeface(typeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);




        lyrics.setTypeface(typeface);

        if(prefs.getBoolean("isNightModeEnabledTrue", false)){
            lyrics.setTextColor(Color.WHITE);
            cardView.setBackgroundColor(Color.parseColor("#29282e"));
        }
        progressBar = (ProgressBar) findViewById(R.id.progressbarVL);


        seekBar=(SeekBar) findViewById(R.id.seekBarVL);
        save=(Button)findViewById(R.id.saveVL);

        save.setTypeface(typeface);
        seekBar.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        imageView.setImageAlpha(seekBar.getProgress());

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

            }
        });


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
                    //new doImg().execute();
                    if("https://deathgrind.club/uploads/posts/2017-09/1506510157_no_cover.png".equals(imageURL)) {
                        imageView.setImageResource(0);

                    }
                    else {
                        Picasso.with(ViewLyrics.this).load(imageURL).into(imageView);
                        menu.getItem(0).setVisible(true);
                        menu.getItem(0).setEnabled(true);
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String lyric = jsonObject.getString("lyric");
                    SpannableString ss1=  new SpannableString(ticker.toUpperCase());
                    ss1.setSpan(new RelativeSizeSpan(1.3f), 0,ticker.length(), 0);


                    if(lyric.isEmpty()){
                        new doIt().execute();
                    }
                    else {
                       // progressDialog.dismiss();
                        lyrics.setTextIsSelectable(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        lyrics.setText(TextUtils.concat(ss1,  "\n\n" , lyric));
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

            SpannableString ss1=  new SpannableString(ticker.toUpperCase());
            ss1.setSpan(new RelativeSizeSpan(1.3f), 0,ticker.length(), 0);
           // progressDialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);

            if(words==null)
                return;

            else if(words.isEmpty()){
                lyrics.setText(TextUtils.concat("\n\n\n\n\n\n\n\n",ss1,"\n\nSorry! No Lyrics found for this song!"));
                return;
            }
            // sendNotification();
            lyrics.setTextIsSelectable(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                lyrics.setText(TextUtils.concat(ss1,"\n\n",Html.fromHtml(words,Html.FROM_HTML_MODE_COMPACT)));
            else
                lyrics.setText(TextUtils.concat(ss1,"\n\n",Html.fromHtml(words)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_recog, menu);
        this.menu=menu;
        menu.getItem(0).setVisible(false);
        menu.getItem(0).setEnabled(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(1).setEnabled(false);
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

        }


        return super.onOptionsItemSelected(item);
    }


}
