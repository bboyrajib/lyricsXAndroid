package com.example.bboyrajib.lyricsx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bboyrajib on 22/07/17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent=new Intent(context,NLService.class);
        context.startService(myIntent);
    }
}
