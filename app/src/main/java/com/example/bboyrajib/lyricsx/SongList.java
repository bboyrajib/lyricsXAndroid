package com.example.bboyrajib.lyricsx;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class SongList extends AppCompatActivity {

    TextView list;
    SharedPreferences prefs;
    String list_songs;
    String createTableURL="http://eurus.96.lt/lyricsx_create_table.php";
    String backupToURL="http://eurus.96.lt/lyricsx_backup_to_table.php";
    String restoreFromURL="http://eurus.96.lt/lyricsx_restore_from_table.php";
    Button backup,restore;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativelayout);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        list=(TextView)findViewById(R.id.list_songs);

        if(!prefs.getBoolean("isAccountSelected",false)||!prefs.getBoolean("isRestored",false))
            pickUserAccount();

        backup=(Button)findViewById(R.id.backup);

        if(prefs.getString("list", null)==null)
            backup.setVisibility(View.GONE);

        if(prefs.getString("list", null)!=null) {
            list_songs = prefs.getString("list", null);
            list.setText(list_songs);
            backup.setVisibility(View.VISIBLE);
        }


        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog=new ProgressDialog(SongList.this);
                progressDialog.setTitle("Backing up your songs");
                progressDialog.setMessage("Please wait a moment");
                progressDialog.setCancelable(false);
                progressDialog.show();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // progressDialog.dismiss();
                            backupMyData();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                String user=prefs.getString("username",null);
                BackupRestorePostRequest request = new BackupRestorePostRequest(createTableURL, user, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SongList.this);
                queue.add(request);
            }
        });




    }

    public void pickUserAccount() {
    /*This will list all available accounts on device without any filtering*/
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, 23);
    }
    /*After manually selecting every app related account, I got its Account type using the code below*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 23) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                progressDialog=new ProgressDialog(SongList.this);
                progressDialog.setTitle("Restoring your songs");
                progressDialog.setMessage("Please wait a moment");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String username;
                username=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                username=username.substring(0,username.indexOf("@")).replace(".","");
             //   Toast.makeText(this,username,Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isAccountSelected",true);
                editor.putString("username",username);
                editor.apply();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                             progressDialog.dismiss();

                            JSONObject jsonObject=new JSONObject(response);
                            String success=jsonObject.getString("success");
                            if(success.equals("restored")) {
                                String data = jsonObject.getString("data");
                                int rows=jsonObject.getInt("count");
                                if (rows == 1) {
                                    Snackbar snackbar = Snackbar.make(relativeLayout, rows+" song has been restored!", Snackbar.LENGTH_LONG);
                                    View sbView = snackbar.getView();
                                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.YELLOW);
                                    snackbar.show();
                                }
                                else {
                                    Snackbar snackbar = Snackbar.make(relativeLayout, rows+" songs have been restored!", Snackbar.LENGTH_LONG);
                                    View sbView = snackbar.getView();
                                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.YELLOW);
                                    snackbar.show();
                                }

                                SharedPreferences.Editor editor=prefs.edit();
                                editor.putBoolean("isRestored",true);
                                editor.putInt("rows",rows);
                                if(prefs.getString("list",null)!=null)
                                    editor.putString("list",prefs.getString("list",null)+"\n\n"+data);
                                else
                                    editor.putString("list",data);

                                editor.apply();

                                if(list.getText().toString().equals("\n\n\n\n\n\n\n\n\n\n\nNo Songs in your History!"))
                                    list.setText(data);
                                else {
                                    list.setText(list.getText() + "\n\n" + data);

                                }

                            }
                            else {
                                SharedPreferences.Editor editor=prefs.edit();
                                editor.putBoolean("isRestored",true);
                                editor.apply();
                                Snackbar snackbar = Snackbar.make(relativeLayout, "No backups found in database!", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();

                            }
                           // restore.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                BackupRestorePostRequest request = new BackupRestorePostRequest(restoreFromURL, username, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SongList.this);
                queue.add(request);

            } else if (resultCode == RESULT_CANCELED) {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Please select an account to continue!", Snackbar.LENGTH_INDEFINITE).setAction("SELECT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickUserAccount();
                    }
                });
                snackbar.setActionTextColor(Color.rgb(71,145,148));
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }
        }
    }

    public void backupMyData(){

        String username=prefs.getString("username",null);
        String data=prefs.getString("list",null);
        String[] arr=data.split("\\n\\n");
        int prev_row=prefs.getInt("rows",0);
        int curr_row=arr.length;
        if(curr_row==prev_row) {

            progressDialog.dismiss();
            Snackbar snackbar = Snackbar.make(relativeLayout, "Already up to date!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
            return;
        }
        for(int i=0;i<curr_row-prev_row;i++) {
            Log.i("Songs: ",arr[i]);
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            BackupRestorePostRequest request = new BackupRestorePostRequest(backupToURL, username, arr[i], listener);
            RequestQueue queue = Volley.newRequestQueue(SongList.this);
            queue.add(request);
        }

        progressDialog.dismiss();
        if((curr_row-prev_row)==1) {

            Snackbar snackbar = Snackbar.make(relativeLayout, (curr_row - prev_row) + " song has been backed up!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
        else {
            Snackbar snackbar = Snackbar.make(relativeLayout, (curr_row - prev_row) + " songs have been backed up!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
       // Toast.makeText(SongList.this, "Backup Successful", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor=prefs.edit();
        editor.putInt("rows",curr_row);
        editor.apply();

    }

}
