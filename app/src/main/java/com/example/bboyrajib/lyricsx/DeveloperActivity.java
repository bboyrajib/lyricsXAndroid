package com.example.bboyrajib.lyricsx;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DeveloperActivity extends AppCompatActivity {


    ImageView fb,gh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        fb=(ImageView)findViewById(R.id.fb);
        gh=(ImageView)findViewById(R.id.gh);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://facebook.com/RjBrOy")));
            }
        });

        gh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/bboyrajib")));
            }
        });
    }
}
