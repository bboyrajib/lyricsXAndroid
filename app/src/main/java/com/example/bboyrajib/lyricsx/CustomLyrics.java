package com.example.bboyrajib.lyricsx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class CustomLyrics extends AppCompatActivity {

    MaterialEditText song,artist;

   // private TextInputEditText song;
   // private TextInputEditText artist;
  //  TextInputLayout tilsong, tilartist;
    RelativeLayout relativeLayout;
    CardView cardView;
    SharedPreferences prefs;

    private TextView clyrics,getLyrics,getLyrics2;
    String song_name,artist_name;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_lyrics);


        cardView=(CardView)findViewById(R.id.cvCS);
        song=(MaterialEditText) findViewById(R.id.song);
        artist=(MaterialEditText) findViewById(R.id.artist);
        getLyrics=(TextView) findViewById(R.id.getLyrics);
        getLyrics2=(TextView)findViewById(R.id.getLyrics2);
        //clyrics=(TextView)findViewById(R.id.customlyrics);
        relativeLayout=(RelativeLayout)findViewById(R.id.relSR);
        prefs= PreferenceManager.getDefaultSharedPreferences(CustomLyrics.this);

       // song.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
       // artist.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
        getLyrics2.setTypeface(typeface);

        song.setFloatingLabelText(Utils.typeface(typeface,"SONG"));
        artist.setFloatingLabelText(Utils.typeface(typeface,"ARTIST (OPTIONAL)"));


        ActionBar actionBar=getSupportActionBar();

        TextView tv = new TextView(getApplicationContext());
        tv.setText(actionBar.getTitle());
        tv.setTextColor(Color.parseColor("#fcfcfc"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        tv.setTypeface(typeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(prefs.getBoolean("isNightModeEnabledTrue",false)){

            cardView.setCardBackgroundColor(Color.parseColor("#29282e"));

            song.setErrorColor(Color.parseColor("#ffa500"));
            artist.setErrorColor(Color.parseColor("#ffa500"));

            song.setPrimaryColor(Color.WHITE);
            artist.setPrimaryColor(Color.WHITE);

            song.setBaseColor(Color.WHITE);
            artist.setBaseColor(Color.WHITE);

            song.setUnderlineColor(Color.WHITE);
            artist.setUnderlineColor(Color.WHITE);

            song.setMetTextColor(Color.YELLOW);
            artist.setMetTextColor(Color.YELLOW);

            song.setMetHintTextColor(Color.WHITE);
            artist.setMetHintTextColor(Color.WHITE);

            setCursorColor(song,Color.YELLOW);
            setCursorColor(artist,Color.YELLOW);


        }



//        clyrics.setTypeface(typeface);
        song.setTypeface(typeface);
        artist.setTypeface(typeface);

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

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }



}
