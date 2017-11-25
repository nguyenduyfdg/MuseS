package com.example.dell.museumguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.model.BeaconView;
import com.example.dell.model.MediaButtonReceiver;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class MediaActivity extends AppCompatActivity implements BeaconConsumer {
    @SuppressLint("StaticFieldLeak")
    public static Activity activity = null;

    public static final String BEACON_TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager = null;
    String id1;
    String id2;
    String id3;

    public static MediaPlayer startup;
    public static MediaPlayer sound;

    RelativeLayout activity_media;
    ImageView background_media;
    ScrollView scrMedia;
    TextView txtTitle;
    TextView txtContent;
    ImageView imgArtifact;
    ImageButton btnPlay;
    ImageButton btnFav;
    TextView txtCurrent;
    TextView txtDuration;
    SeekBar seekBar;

    static UpdateCurrent updateCurrent;

    String path;

    String media;
    String start;
    String title;
    String image;
    String content;
    boolean favorite;
    int id;
    String address;

    String DATABASE_NAME = "dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    boolean auto;
    boolean dark;
    String background;
    String language;

    boolean userTouch = false;
    static boolean screenOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        id = intent.getIntExtra("id",100);

        activity = this;

        processCopy();
        getSettingsFromDatabase();
        getDataFromDatabase();

        addControls();
        addEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        screenOn = true;

        updateCurrent = new UpdateCurrent();
        updateCurrent.execute();

        if (auto){
            beaconConfigure();

            if (!beaconManager.isBound(this)){
                beaconManager.bind(this);
            }
        }
    }

    private void addEvents() {
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sound.seekTo(0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int current;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    current = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userTouch = false;
                sound.seekTo(current);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startup.stop();

                if (sound.isPlaying()){
                    sound.pause();
                }
                else {
                    sound.start();
                }
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorite){
                    favorite = false;
                    switch (language){
                        case "english":
                            Toast.makeText(getApplicationContext(),"Removed from Favorites",Toast.LENGTH_SHORT).show();
                            break;
                        case "vietnamese":
                            Toast.makeText(getApplicationContext(),"Đã xóa khỏi Danh sách Yêu thích",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                else {
                    favorite = true;
                    switch (language){
                        case "english":
                            Toast.makeText(getApplicationContext(),"Added to Favorites",Toast.LENGTH_SHORT).show();
                            break;
                        case "vietnamese":
                            Toast.makeText(getApplicationContext(),"Đã thêm vào Danh sách Yêu thích",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                ContentValues contentValues = new ContentValues();
                if (favorite){
                    contentValues.put("favorite",1);
                }
                else {
                    contentValues.put("favorite",0);
                }
                database.update(path,contentValues,"id=?",new String[]{id+""});
            }
        });
    }

    private void addControls() {
        ((AudioManager)getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(
                new ComponentName(
                        getPackageName(),
                        MediaButtonReceiver.class.getName()));

        activity_media = (RelativeLayout) findViewById(R.id.activity_media);

        background_media = (ImageView) findViewById(R.id.background_media);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);

        imgArtifact = (ImageView) findViewById(R.id.imgArtifact);

        String cont = null;
        try {
            // Get background image
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_media.setImageDrawable(drawable);

            // Get artifact's content
            InputStream inputStream1 = getAssets().open(path+"/"+language+"/"+content);
            int size1 = inputStream1.available();
            byte[] buf1 = new byte[size1];
            inputStream1.read(buf1);
            inputStream1.close();
            cont = new String(buf1);

            // Get artifact's sound content
            AssetFileDescriptor afd = getAssets().openFd(path+"/"+language+"/"+media);
            sound = new MediaPlayer();
            sound.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            sound.prepare();
            sound.start();
            SystemClock.sleep(50);
            sound.pause();

            // Get artifact's sound name
            AssetFileDescriptor afd1 = getAssets().openFd(start);
            startup = new MediaPlayer();
            startup.setDataSource(afd1.getFileDescriptor(),afd1.getStartOffset(),afd1.getLength());
            startup.prepare();
            startup.start();

            // Get artifact's image
            InputStream ims = getAssets().open(image);
            Drawable d = Drawable.createFromStream(ims, null);
            imgArtifact.setImageDrawable(d);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        txtContent = (TextView) findViewById(R.id.txtContent);
        txtContent.setText(cont); // Show artifact's content

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);

        btnFav = (ImageButton) findViewById(R.id.btnFav);

        txtCurrent = (TextView) findViewById(R.id.txtCurrent);

        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtDuration.setText(msToString(sound.getDuration()));

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(sound.getDuration());

        // Set color base on Dark Mode on or off
        if (dark){
            txtTitle.setTextColor(Color.WHITE);
            txtContent.setTextColor(Color.WHITE);
            txtCurrent.setTextColor(Color.WHITE);
            txtDuration.setTextColor(Color.WHITE);
            activity_media.setBackgroundResource(android.R.color.black);
        }
        else {
            txtTitle.setTextColor(Color.BLACK);
            txtContent.setTextColor(Color.BLACK);
            txtCurrent.setTextColor(Color.BLACK);
            txtDuration.setTextColor(Color.BLACK);
            activity_media.setBackgroundResource(android.R.color.white);
        }

        scrMedia = (ScrollView) findViewById(R.id.scrMedia);
    }

    private void getBeaconIdentifiers(String address) {
        String[] id = address.split("/");
        if (id.length >= 3) {
            id1 = id[0];
            id2 = id[1];
            id3 = id[2];
        }
    }

    private void beaconConfigure() {
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());

        // Detect the iBeacons:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region(
                "myBeacon",
                Identifier.parse(id1),
                Identifier.parse(id2),
                Identifier.parse(id3)
        );
        Log.i(BEACON_TAG,"onBeaconServiceConnect");

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.i(BEACON_TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(BEACON_TAG,"notEnterRegion");
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.i(BEACON_TAG, "didExitRegion");

                    if (!sound.isPlaying()) {
                        updateCurrent.cancel(true);
                        screenOn = false;

                        sound.release();
                        startup.release();

                        beaconManager.stopRangingBeaconsInRegion(region);

                        beaconManager.unbind(MediaActivity.this);
                        beaconManager.removeAllMonitorNotifiers();
                        beaconManager.removeAllRangeNotifiers();

                        Log.i(BEACON_TAG, "finish()");
                        finish();
                    }
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(BEACON_TAG,"notExitRegion");
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(BEACON_TAG,"didDetermineStateForRegion");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                    Log.i(BEACON_TAG,"startRangingBeaconsInRegion");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i(BEACON_TAG,"didRangeBeaconsInRegion " + beacons.size());
                if (beacons.size() > 0){
                    for (Beacon oneBeacon : beacons) {
                        Log.i(
                                BEACON_TAG,
                                " RSSI: " +
                                        oneBeacon.getRssi() +
                                        " ID: " +
                                        oneBeacon.getId1() +
                                        "/" +
                                        oneBeacon.getId2() +
                                        "/" +
                                        oneBeacon.getId3()
                        );
                    }
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
            Log.i(BEACON_TAG,"startMonitoringBeaconsInRegion");
        } catch (RemoteException e) {
            Log.i(BEACON_TAG,"stopMonitoringBeaconsInRegion");
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateCurrent extends AsyncTask<Void,Integer,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Integer iconPlay;
            Integer iconFav;

            while (screenOn){
                if (sound.isPlaying()){
                    if (dark){
                        iconPlay = R.drawable.pause_white;
                    }
                    else {
                        iconPlay = R.drawable.pause_black;
                    }
                }
                else {
                    if (dark){
                        iconPlay = R.drawable.play_white;
                    }
                    else {
                        iconPlay = R.drawable.play_black;
                    }
                }

                if (favorite){
                    iconFav = R.drawable.star_checked;
                }
                else {
                    if (dark){
                        iconFav = R.drawable.star_unchecked_white;
                    }
                    else {
                        iconFav = R.drawable.star_unchecked_black;
                    }
                }

                Integer current = sound.getCurrentPosition();
                publishProgress(current,iconPlay,iconFav);
                SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Integer current = values[0];
            Integer iconPlay = values[1];
            Integer iconFav = values[2];

            txtCurrent.setText(msToString(current));

            if (!userTouch){
                seekBar.setProgress(current);
            }

            btnPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),iconPlay));

            btnFav.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),iconFav));
        }
    }

    @SuppressLint("DefaultLocale")
    private String msToString (long ms){
        long min = TimeUnit.MILLISECONDS.toMinutes(ms);
        long sec = TimeUnit.MILLISECONDS.toSeconds(ms) - (min*60);
        return String.format("%02d:%02d",min,sec);
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Settings",null);
        cursor.moveToFirst();
        do {
            auto = cursor.getInt(0) > 0;
            dark = cursor.getInt(1) > 0;
            background = cursor.getString(2);
            language = cursor.getString(3);
        }
        while (cursor.moveToNext());
        cursor.close();
    }

    private void getDataFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " +path+ " WHERE id=?",new String[]{id+""});
        while (cursor.moveToNext()){
            image = cursor.getString(3);
            title = cursor.getString(1);
            content = cursor.getString(2);
            start = cursor.getString(5);
            media = cursor.getString(4);
            favorite = cursor.getInt(7) != 0;
            address = cursor.getString(6);
        }
        cursor.close();

        getBeaconIdentifiers(address);
    }

    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()){
            try{
                CopyDataBaseFromAsset();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;

            myInput = getAssets().open(DATABASE_NAME);

            //Path to the just created empty db
            String outFileName = getDatabasePath();

            //if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the input file to the output file
            byte[] buffer = new byte[2048];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getDatabasePath(){
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        assert dir != null;
        return dir.delete();
    }

    public static void mediaDoubleClick(){
        updateCurrent.cancel(true);
        screenOn = false;

        sound.release();
        startup.release();

        activity.finish();
    }

    @Override
    public void onBackPressed() {
        updateCurrent.cancel(true);
        screenOn = false;

        sound.release();
        startup.release();

        if (auto){
            if (beaconManager.isBound(this)){
                beaconManager.unbind(this);
                beaconManager.removeAllMonitorNotifiers();
                beaconManager.removeAllRangeNotifiers();
            }
        }

        ((AudioManager)getSystemService(AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(
                new ComponentName(
                        getPackageName(),
                        MediaButtonReceiver.class.getName()));

        deleteCache(this);

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        screenOn = false;

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        updateCurrent.cancel(true);
        screenOn = false;

        sound.release();
        startup.release();

        if (auto){
            if (beaconManager.isBound(this)){
                beaconManager.unbind(this);
                beaconManager.removeAllMonitorNotifiers();
                beaconManager.removeAllRangeNotifiers();
            }
        }

        ((AudioManager)getSystemService(AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(
                new ComponentName(
                        getPackageName(),
                        MediaButtonReceiver.class.getName()));

        deleteCache(this);

        super.onDestroy();
    }
}
