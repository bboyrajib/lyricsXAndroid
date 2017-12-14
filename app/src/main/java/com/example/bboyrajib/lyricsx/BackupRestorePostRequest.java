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

    public BackupRestorePostRequest(String URL,String username,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
    }
    public BackupRestorePostRequest(String URL,String username,String data,Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("data",data);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
