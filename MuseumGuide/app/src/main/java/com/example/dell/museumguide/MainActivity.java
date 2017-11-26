package com.example.dell.museumguide;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    Context context = this;

    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    boolean auto;
    boolean dark;
    String background;
    String language;

    RelativeLayout activity_main;
    ImageView background_main;
    LinearLayout layoutMuseum;
    ImageView imgMuseum;
    TextView txtMuseum;
    LinearLayout layoutMap;
    ImageView imgMap;
    TextView txtMap;
    LinearLayout layoutFavorite;
    ImageView imgFavorite;
    TextView txtFavorite;
    LinearLayout layoutSettings;
    ImageView imgSettings;
    TextView txtSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processCopy();
        getSettingsFromDatabase();
        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.MEDIA_CONTENT_CONTROL,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSettingsFromDatabase();

        addControls();
        addEvents();
    }

    private void addEvents() {
        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        layoutMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MuseumsActivity.class);
                startActivity(intent);
            }
        });

        layoutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        layoutFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FavoritesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        background_main = (ImageView) findViewById(R.id.background_main);
        layoutMuseum = (LinearLayout) findViewById(R.id.layoutMuseum);
        imgMuseum = (ImageView) findViewById(R.id.imgMuseum);
        txtMuseum = (TextView) findViewById(R.id.txtMuseum);
        layoutMap = (LinearLayout) findViewById(R.id.layoutMap);
        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtMap = (TextView) findViewById(R.id.txtMap);
        layoutFavorite = (LinearLayout) findViewById(R.id.layoutFavorite);
        imgFavorite = (ImageView) findViewById(R.id.imgFavorite);
        txtFavorite = (TextView) findViewById(R.id.txtFavorite);
        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);
        imgSettings = (ImageView) findViewById(R.id.imgSettings);
        txtSettings = (TextView) findViewById(R.id.txtSettings);

        if (dark){
            activity_main.setBackgroundResource(android.R.color.black);

            imgMuseum.setImageResource(R.drawable.museum_white);
            txtMuseum.setTextColor(Color.WHITE);

            imgMap.setImageResource(R.drawable.location_white);
            txtMap.setTextColor(Color.WHITE);

            imgFavorite.setImageResource(R.drawable.star_white);
            txtFavorite.setTextColor(Color.WHITE);

            imgSettings.setImageResource(R.drawable.settings_white);
            txtSettings.setTextColor(Color.WHITE);
        }
        else {
            activity_main.setBackgroundResource(android.R.color.white);

            imgMuseum.setImageResource(R.drawable.museum_black);
            txtMuseum.setTextColor(Color.BLACK);

            imgMap.setImageResource(R.drawable.location_black);
            txtMap.setTextColor(Color.BLACK);

            imgFavorite.setImageResource(R.drawable.star_black);
            txtFavorite.setTextColor(Color.BLACK);

            imgSettings.setImageResource(R.drawable.settings_black);
            txtSettings.setTextColor(Color.BLACK);
        }

        switch (language){
            case "english":
                txtMuseum.setText(R.string.museums_main_en);
                txtMap.setText(R.string.maps_main_en);
                txtFavorite.setText(R.string.favorites_main_en);
                txtSettings.setText(R.string.settings_main_en);
                break;
            case "vietnamese":
                txtMuseum.setText(R.string.museums_main_vi);
                txtMap.setText(R.string.maps_main_vi);
                txtFavorite.setText(R.string.favorites_main_vi);
                txtSettings.setText(R.string.settings_main_vi);
                break;
        }

        try {
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_main.setImageDrawable(drawable);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM Settings", null);
        cursor.moveToFirst();
        do {
            auto = cursor.getInt(0) != 0;
            dark = cursor.getInt(1) != 0;
            background = cursor.getString(2);
            language = cursor.getString(3);
        }
        while (cursor.moveToNext());
        cursor.close();
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
            byte[] buffer = new byte[1024];
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

    @Override
    public void onBackPressed() {
        deleteCache(context);

        super.onBackPressed();
    }
}
