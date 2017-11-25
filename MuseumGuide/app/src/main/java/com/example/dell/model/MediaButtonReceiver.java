package com.example.dell.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.example.dell.museumguide.MediaActivity;

/**
 * Created by admin on 7/4/2017.
 */

public class MediaButtonReceiver extends BroadcastReceiver {
    String TAG = "MediaButtonPressed";

    static final long CLICK_DELAY = 500;
    static long lastClick = 0; // oldValue
    static long currentClick = System.currentTimeMillis();

    public MediaButtonReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            return;
        }
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            return;
        }
        int action = event.getAction();

        if (action != KeyEvent.ACTION_UP)return;

        currentClick = System.currentTimeMillis();
        Log.i(TAG, "currentClick" + currentClick);

        if(currentClick - lastClick < CLICK_DELAY ){
            // double click
            Log.i(TAG, "Double Click");

            MediaActivity.mediaDoubleClick();
        }
        else {
            // single click
            Log.i(TAG, "Single Click");

            MediaActivity.startup.stop();

            if (MediaActivity.sound.isPlaying()){
                MediaActivity.sound.pause();
            }
            else {
                MediaActivity.sound.start();
            }
        }

        Log.i(TAG, "old lastClick" + lastClick);
        lastClick = currentClick ;
        Log.i(TAG, "new lastClick" + lastClick);

        abortBroadcast();
    }
}
