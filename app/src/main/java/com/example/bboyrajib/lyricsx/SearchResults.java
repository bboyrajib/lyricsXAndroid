package com.example.bboyrajib.lyricsx;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResults extends AppCompatActivity {

    Typeface typeface;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    TextView textView;
    ProgressBar progressBar;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;
    String song,artist;
    JSONArray tracks,artists,albums,uris,images;
   // String [] songs,artists,albums,uris,images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        typeface
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerViewSR);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        relativeLayout=(RelativeLayout)findViewById(R.id.relSR);
        listItems=new ArrayList<>();

        textView=(TextView)findViewById(R.id.emptyresult);
        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
        textView.setText("\n\n\n\n\n\n\n\n\n\n\nSearching database!");
        textView.setTypeface(typeface);
        progressBar = (ProgressBar) findViewById(R.id.progressbarSR);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent=getIntent();
        song=intent.getStringExtra("song");
        if(intent.getStringExtra("artist")!=null)
          artist=intent.getStringExtra("artist");


        search(song,artist);



    }

    private void search(String song,String artist){
        String URL;
        song=song.replaceAll("\\s","+");
        if(artist!=null)
            artist=artist.replaceAll("\\s","+");
        if(artist==null || artist.isEmpty() || "+".equals(artist))
             URL="http://eurus.96.lt/spotify/tracks.php?song="+song.toLowerCase();
        else
             URL="http://eurus.96.lt/spotify/tracks.php?song="+song.toLowerCase()+"&artist="+artist.toLowerCase();
        Log.i("URL",URL);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    progressBar.setVisibility(View.INVISIBLE);
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getBoolean("success")){
                        textView.setText("\n\n\n\n\n\n\n\n\n\n\nLoading search results!\n\nPlease wait!");
                       // textView.setVisibility(View.GONE);
                         tracks=jsonObject.getJSONArray("track");
                         artists=jsonObject.getJSONArray("artist");
                         albums=jsonObject.getJSONArray("album");
                         uris=jsonObject.getJSONArray("uri");
                         images=jsonObject.getJSONArray("image");
                      //  Log.i("tag",artist.get(0).toString()+"");
                        for(int i=0;i<tracks.length();i++){

                            ListItem listItem=new ListItem(tracks.get(i).toString(),artists.get(i).toString(),albums.get(i).toString(),uris.get(i).toString(),"0","0",images.get(i).toString().replace("\\",""));
                            // Log.i("test",songs[i]+" "+artists[i]);
                            listItems.add(listItem);

                        }


                        textView.setVisibility(View.GONE);
                        RecyclerViewAdapter.RecyclerViewLongClickListener longClickListener=new RecyclerViewAdapter.RecyclerViewLongClickListener() {
                            @Override
                            public void onLongClick(View v, int position) {
                                if(appInstalledOrNot("com.spotify.music")) {
                                    Intent intent=null;
                                    try {
                                      //  Log.i("tag",uris.get(position).toString().substring(uris.get(position).toString().lastIndexOf(":")));
                                        intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse( uris.get(position).toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(intent);
                                }
                                else {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://open.spotify.com/track/" + uris.get(position).toString().substring(uris.get(position).toString().lastIndexOf(":")+1))));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;

                            }
                        };

                        RecyclerViewAdapter.RecyclerViewClickListener listener=new RecyclerViewAdapter.RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                                Intent intent=new Intent(SearchResults.this,ViewLyrics.class);
                                try {
                                    intent.putExtra("clickSong",tracks.get(position).toString());
                                    intent.putExtra("clickArtist",artists.get(position).toString());
                                    intent.putExtra("imageURL",images.get(position).toString());
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        adapter=new RecyclerViewAdapter(listItems,getApplicationContext(),listener,longClickListener);
                        recyclerView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();



                    }
                    else
                        textView.setText("\n\n\n\n\n\n\n\n\n\n\nNo matching results!");



                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SearchResults.this,"Catch: 404",Toast.LENGTH_SHORT).show();
                }
            }
        };
        LyricsRequest request = new LyricsRequest(URL, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SearchResults.this);
        queue.add(request);


    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }






}
