package com.example.bboyrajib.lyricsx;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.widget.TextView;

public class CopyrightActivity extends AppCompatActivity {



    CardView cardView;
    TextView tv1,tv2,tv3,tv4;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);



        cardView=(CardView)findViewById(R.id.cardCopyright);
        tv1=(TextView)findViewById(R.id.lyricsCopy);
        tv2=(TextView)findViewById(R.id.lyricsPowered);
        tv3=(TextView)findViewById(R.id.arcHead);
        tv4=(TextView)findViewById(R.id.arcPowered);


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
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv1.setTypeface(typeface);
        tv2.setTypeface(typeface);
        tv3.setTypeface(typeface);
        tv4.setTypeface(typeface);

        prefs= PreferenceManager.getDefaultSharedPreferences(CopyrightActivity.this);

        if(prefs.getBoolean("isNightModeEnabledTrue",false)){
            cardView.setCardBackgroundColor(Color.parseColor("#29282e"));
            tv1.setTextColor(Color.WHITE);
            tv2.setTextColor(Color.WHITE);
            tv3.setTextColor(Color.WHITE);
            tv4.setTextColor(Color.WHITE);
        }




    }
}
