package com.example.bboyrajib.lyricsx;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bboyrajib on 14/12/17.
 */

public class BackupRestorePostRequest extends StringRequest {

    private Map<String, String> params;

    public BackupRestorePostRequest(String URL,String ID,int nothing,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("ID",ID);
    }

    public BackupRestorePostRequest(String URL,String username,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
    }
    public BackupRestorePostRequest(String URL,String username,String DB_ID,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("db_id",DB_ID);
    }
    public BackupRestorePostRequest(String URL,String username,String song, String artist, String album, String ID, String has_lyric, String imageUrl,String timestamp, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("song",song);
        params.put("artist",artist);
        params.put("album",album);
        params.put("id",ID);
        params.put("has_lyric",has_lyric);
        params.put("imageUrl",imageUrl);
        params.put("timestamp",timestamp);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
