package com.example.bboyrajib.lyricsx;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboyrajib on 15/12/17.
 */

public class SongListRecyclerView extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;
    String listSongs,listArtists,listAlbums;
  //  String createTableURL="http://eurus.96.lt/lyricsx_create_table.php";
  //  String backupToURL="http://eurus.96.lt/lyricsx_backup_to_table.php";
    String restoreFromURL="http://bboyrajibx.xyz/lyricsx_restore_from_table.php";
    String updateTableURL="http://bboyrajibx.xyz/lyricsx_update_table.php";
    String deleteAllURL="http://bboyrajibx.xyz/lyricsx_delete_all.php";
    TextView empty;
    SharedPreferences prefs;
    RelativeLayout relativeLayout;
    ProgressDialog progressDialog;
    int prev_row,curr_row,count=0,i;
    String song,artist,album,ID,has_lyric,DB_ID,imageURL,TimeStamp;
    String [] songs,artists,albums,IDs,has_lyrics,DB_IDs,imageURLs,TimeStamps;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    Typeface typeface;
    CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_recycler_view);

        typeface
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardView=(CardView)findViewById(R.id.cvResults);


        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       // else
            recyclerView.setAdapter(adapter);

        relativeLayout=(RelativeLayout)findViewById(R.id.relandrecycle);

        listItems=new ArrayList<>();

        empty=(TextView)findViewById(R.id.emptylist);
        Typeface typeface
                = Typeface.createFromAsset(
                getAssets(), "Pangolin-Regular.ttf");
        empty.setTypeface(typeface);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if(!prefs.getBoolean("isAccountSelected",false)) {
            return;

        }
      //  if(!prefs.getBoolean("isRestored",false)){
         /*   progressDialog = new ProgressDialog(SongListRecyclerView.this);
            progressDialog.setTitle("Loading your search results!");
            progressDialog.setMessage("Please wait a moment");
            progressDialog.setCancelable(true);
            progressDialog.show();*/

         progressBar.setVisibility(View.VISIBLE);


       // }
            swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    restoreFunc();
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    restoreFunc();
                }
            });


      /*  if(prefs.getString("listSong", null)!=null){

            listSongs=prefs.getString("listSong",null);
            listArtists=prefs.getString("listArtist",null);
            listAlbums=prefs.getString("listAlbum",null);
            String[] songs=listSongs.split("\\n\\n");
            String[] artists=listArtists.split("\\n\\n");
            String[] albums=listAlbums.split("\\n\\n");

            for(int i=0;i<songs.length;i++){
                ListItem listItem=new ListItem(songs[i],artists[i],albums[i]);
                Log.i("test",songs[i]+" "+artists[i]);
                listItems.add(listItem);
            }
            empty.setVisibility(View.GONE);
            //listItems.clear();
            adapter=new RecyclerViewAdapter(listItems,getApplicationContext());
            recyclerView.setAdapter(adapter);



        }*/
       /* if(prefs.getBoolean("isRestored",false)) {
            Log.i("Backup","Backingup");
            backUpUserData();
        }*/




    }

  /*  public void pickUserAccount() {
    //This will list all available accounts on device without any filtering
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, 23);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 23) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SongListRecyclerView.this);
                progressDialog.setTitle("Restoring your songs");
                progressDialog.setMessage("Please wait a moment");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String username;
                username = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                username = username.substring(0, username.indexOf("@")).replace(".", "");
                //   Toast.makeText(this,username,Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isAccountSelected", true);
                editor.putString("username", username);
                editor.apply();

                restoreFunc();
            } else if (resultCode == RESULT_CANCELED) {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Please select an account to continue!", Snackbar.LENGTH_INDEFINITE).setAction("SELECT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickUserAccount();
                    }
                });
                snackbar.setActionTextColor(Color.rgb(71, 145, 148));
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }
        }
    }*/

  /*  private void backUpUserData(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // progressDialog.dismiss();
                    backupData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        String user=prefs.getString("username",null);
        BackupRestorePostRequest request = new BackupRestorePostRequest(createTableURL, user, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SongListRecyclerView.this);
        queue.add(request);
    }

    private void backupData(){

        String username=prefs.getString("username",null);
        String song=prefs.getString("listSong",null);
        String artist=prefs.getString("listArtist",null);
        String album=prefs.getString("listAlbum",null);
        String trackID=prefs.getString("trackID",null);
        String[] songs=song.split("\\n\\n");
        String [] artists=artist.split("\\n\\n");
        String [] albums=album.split("\\n\\n");
        String[] trackIDs=trackID.split("\\n\\n");
         prev_row=prefs.getInt("rows",0);
         curr_row=songs.length;
        if(curr_row==prev_row)
            return;
        for( i=curr_row-prev_row-1;i>=0;i--) {
           // Log.i("Songs: ",arr[i]);
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {


                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            };
            Log.i("requests to server"," " +songs[i]);
            BackupRestorePostRequest request = new BackupRestorePostRequest(backupToURL, username, songs[i],artists[i],albums[i],trackIDs[i], listener);
            RequestQueue queue = Volley.newRequestQueue(SongListRecyclerView.this);
            queue.add(request);
        }

      //  Log.i("Count: ",""+count);


        if((curr_row-prev_row)==1) {

            Snackbar snackbar = Snackbar.make(relativeLayout, (curr_row-prev_row) + " song has been backed up!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
        else {
            Snackbar snackbar = Snackbar.make(relativeLayout, (curr_row-prev_row) + " songs have been backed up!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

        // Toast.makeText(SongList.this, "Backup Successful", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt("rows",curr_row);
        editor.apply();



    }*/

    private void restoreFunc(){


        listItems.clear();



        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                   // progressDialog.dismiss();

                    progressBar.setVisibility(View.INVISIBLE);

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("restored")) {

                         song = jsonObject.getString("song");
                         artist = jsonObject.getString("artist");
                         album=jsonObject.getString("album");
                         ID = jsonObject.getString("id");
                         has_lyric=jsonObject.getString("has_lyric");
                        DB_ID=jsonObject.getString("db_id");
                        imageURL=jsonObject.getString("imageUrl");
                        TimeStamp=jsonObject.getString("timestamp");
                        int rows = jsonObject.getInt("count");
                      /*  if (rows == 1) {
                            Snackbar snackbar = Snackbar.make(relativeLayout,   "1 song has been restored!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(relativeLayout, rows + " songs have been restored!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }*/

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isRestored", true);
                        editor.putInt("rows", rows);
                       /* if (prefs.getString("listSong", null) != null) {
                            editor.putString("listSong", prefs.getString("listSong", null) + "\n\n" + song);
                            editor.putString("listArtist", prefs.getString("listArtist", null) + "\n\n" + artist);
                            editor.putString("listAlbum", prefs.getString("listAlbum", null) + "\n\n" + album);
                            editor.putString("trackID", prefs.getString("trackID", null) + "\n\n" + ID);
                            editor.apply();

                            String[] songs=song.split("\\n\\n");
                            String[] artists=artist.split("\\n\\n");
                            String[] albums=album.split("\\n\\n");

                            for(int i=0;i<songs.length;i++){
                                ListItem listItem=new ListItem(songs[i],artists[i],albums[i]);
                                Log.i("test",songs[i]+" "+artists[i]);
                                listItems.add(listItem);
                            }
                            empty.setVisibility(View.GONE);
                            //listItems.clear();
                            adapter=new RecyclerViewAdapter(listItems,getApplicationContext());
                            recyclerView.setAdapter(adapter);

                        }
                        else {*/
                          //  editor.putString("listSong", song);
                          //  editor.putString("listArtist", artist);
                          //  editor.putString("listAlbum", album);
                          //  editor.putString("trackID", ID);
                          //  editor.apply();

                         //   listSongs=prefs.getString("listSong",null);
                         //   listArtists=prefs.getString("listArtist",null);
                         //   listAlbums=prefs.getString("listAlbum",null);
                             songs=song.split("\\n\\n");
                             artists=artist.split("\\n\\n");
                             albums=album.split("\\n\\n");
                             IDs=ID.split("\\n\\n");
                             has_lyrics=has_lyric.split("\\n\\n");
                             DB_IDs=DB_ID.split("\\n\\n");
                             imageURLs=imageURL.split("\\n\\n");
                            TimeStamps=TimeStamp.split("\\n\\n");

                            for(int i=0;i<songs.length;i++){
                                ListItem listItem=new ListItem(songs[i],artists[i],albums[i],IDs[i],has_lyrics[i],DB_IDs[i],imageURLs[i],TimeStamps[i]);
                                Log.i("test",songs[i]+" "+artists[i]);
                                listItems.add(listItem);
                            }
                            empty.setVisibility(View.GONE);

                            //listItems.clear();


                            RecyclerViewAdapter.RecyclerViewLongClickListener longClickListener = new RecyclerViewAdapter.RecyclerViewLongClickListener() {
                                @Override
                                public void onLongClick(View v, final int position) {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(SongListRecyclerView.this);

                                    builder.setMessage(Utils.typeface(typeface, "Are you sure you want to delete this?"))
                                            .setTitle(TextUtils.concat(Utils.typeface(typeface, "Delete "), Utils.typefaceColor("#1a237e", Utils.typeface(typeface, songs[position]))))
                                            .setCancelable(true)
                                            .setNegativeButton(Utils.typefaceColor("#1a237e", Utils.typeface(typeface, "YES")), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    updateDB(position);
                                                }
                                            })
                                            .setPositiveButton(Utils.typefaceColor("#1a237e", Utils.typeface(typeface, "NO")), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            });
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            Button pos = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                                            pos.setTypeface(typeface);
                                            pos.setTextColor(Color.parseColor("#1a237e"));
                                            Button neg = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
                                            neg.setTypeface(typeface);
                                            neg.setTextColor(Color.parseColor("#1a237e"));
                                        }
                                    });
                                    alertDialog.show();

                                }
                            };
                            RecyclerViewAdapter.RecyclerViewClickListener listener = new RecyclerViewAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    createAlertDialog(has_lyrics[position], IDs[position], songs[position], artists[position], position, imageURLs[position]);
                                }


                            };


                            adapter = new RecyclerViewAdapter(listItems, getApplicationContext(), listener, longClickListener);
                            recyclerView.setAdapter(adapter);


                       // }

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                       /* if(prefs.getBoolean("isNightModeEnabledTrue",false)){
                            relativeLayout.setBackgroundColor(Color.WHITE);
                            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#311b92"), PorterDuff.Mode.SRC_IN);
                        }*/



                        //Update RecyclerView here



                    } else {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isRestored", true);
                        editor.apply();
                        empty.setVisibility(View.VISIBLE);
                        Snackbar snackbar = Snackbar.make(relativeLayout, "No backups found in database!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);

                    }
                    // restore.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        BackupRestorePostRequest request = new BackupRestorePostRequest(restoreFromURL, prefs.getString("username",null), responseListener);
        RequestQueue queue = Volley.newRequestQueue(SongListRecyclerView.this);
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

    private void createAlertDialog(String has_lyric, String has_ID, final String song, final String artist, final int position, final String URL){

        CharSequence cs=new String(songs[position]);
        SpannableString spannableString=Utils.typefaceColor("#1a237e",cs);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Utils.typeface(typeface,spannableString));

        if(has_lyric.equals("0") && has_ID.equals("NOID")){
            builder.setMessage(Utils.typeface(typeface," Sorry! No Lyric or Spotify ID found!"))
                    .setCancelable(true)
                    .setPositiveButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"DONE")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
        }
        else if(has_lyric.equals("0")){
            builder.setMessage(Utils.typeface(typeface," No Lyrics found! Do you want to play this song?"))
                    .setCancelable(true)
                    .setPositiveButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"PLAY SONG")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(appInstalledOrNot("com.spotify.music")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("spotify:track:" + IDs[position]));
                                startActivity(intent);
                            }
                            else
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://open.spotify.com/track/"+IDs[position])));
                        }
                    });
        }
        else if(has_ID.equals("NOID")){
            builder.setMessage(Utils.typeface(typeface," Can't play this song! Do you want to view the lyrics?"))
                    .setCancelable(true)
                    .setPositiveButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"VIEW LYRICS")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(SongListRecyclerView.this,ViewLyrics.class);
                            intent.putExtra("clickSong",song);
                            intent.putExtra("clickArtist",artist);
                            intent.putExtra("imageURL",URL);
                            startActivity(intent);
                        }
                    });
        }
        else {
            builder.setMessage(Utils.typeface(typeface," What do you want to do?"))
                    .setCancelable(true)
                    .setPositiveButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"PLAY SONG")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(appInstalledOrNot("com.spotify.music")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("spotify:track:" + IDs[position]));
                                startActivity(intent);
                            }
                            else
                                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://open.spotify.com/track/"+IDs[position])));

                        }
                    })
                    .setNegativeButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"VIEW LYRICS")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(SongListRecyclerView.this,ViewLyrics.class);
                            intent.putExtra("clickSong",song);
                            intent.putExtra("clickArtist",artist);
                            intent.putExtra("imageURL",URL);
                            startActivity(intent);
                        }
                    });
        }



        final AlertDialog alertDialog=builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button pos=alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                pos.setTypeface(typeface);
                pos.setTextColor(Color.parseColor("#1a237e"));
                Button neg=alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
                neg.setTypeface(typeface);
                neg.setTextColor(Color.parseColor("#1a237e"));
            }
        });
        alertDialog.show();
    }

    private void updateDB(int pos){

        progressBar.setVisibility(View.VISIBLE);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(new JSONObject(response).getBoolean("success"))
                        restoreFunc();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };
       // Log.i("All",title+" "+album+" "+artist+" "+trackID+" "+has_lyric);
        //  Log.i("requests to server"," " +songs[i]);
        BackupRestorePostRequest request = new BackupRestorePostRequest(updateTableURL, prefs.getString("username",null), DB_IDs[pos], listener);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(SongListRecyclerView.this);
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteAll) {
            AlertDialog.Builder builder=new AlertDialog.Builder(SongListRecyclerView.this);

            builder.setMessage(Utils.typeface(typeface,"Are you sure you want to delete the entire history?"))
                    .setTitle(TextUtils.concat(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"Delete All !"))))
                    .setCancelable(true)
                    .setNegativeButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"YES")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAll();
                        }
                    })
                    .setPositiveButton(Utils.typefaceColor("#1a237e",Utils.typeface(typeface,"NO")), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            final AlertDialog alertDialog=builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button pos=alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                    pos.setTypeface(typeface);
                    pos.setTextColor(Color.parseColor("#1a237e"));
                    Button neg=alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
                    neg.setTypeface(typeface);
                    neg.setTextColor(Color.parseColor("#1a237e"));
                }
            });
            alertDialog.show();
        }


        return super.onOptionsItemSelected(item);
    }

    private void deleteAll(){

        progressBar.setVisibility(View.VISIBLE);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(new JSONObject(response).getBoolean("success"))
                        restoreFunc();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };
        // Log.i("All",title+" "+album+" "+artist+" "+trackID+" "+has_lyric);
        //  Log.i("requests to server"," " +songs[i]);
        BackupRestorePostRequest request = new BackupRestorePostRequest(deleteAllURL, prefs.getString("username",null), listener);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(SongListRecyclerView.this);
        queue.add(request);



    }


}
