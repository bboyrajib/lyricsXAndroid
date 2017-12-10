package com.example.bboyrajib.lyricsx;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bboyrajib on 08/12/17.
 */

public class LyricsRequest extends StringRequest {

    public LyricsRequest(String URL, Response.Listener<String> listener) {
        super(Method.GET, URL, listener, null);

    }
}
