package com.example.bboyrajib.lyricsx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

public class SongList extends AppCompatActivity {

    TextView list;
    SharedPreferences prefs;
    String list_songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        list=(TextView)findViewById(R.id.list_songs);

        if(prefs.getString("list", null)==null) return;
        list_songs = prefs.getString("list", null);
        list.setText(list_songs);



    }


}
