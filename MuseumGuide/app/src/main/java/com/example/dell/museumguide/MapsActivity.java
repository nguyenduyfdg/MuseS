package com.example.dell.museumguide;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    String DATABASE_NAME = "dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;

    String name;
    String image;
    String table;
    String address;
    double latitude;
    double longitude;

    String language;

    ProgressDialog loadingMap;
    boolean firstOpen = true;
    private GoogleMap mMap;
    GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            if (firstOpen) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.5f));
                firstOpen = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        processCopy();
        getSettingsFromDatabase();
        loadingMap = new ProgressDialog(MapsActivity.this);
        switch (language) {
            case "english":
                loadingMap.setTitle("Notification");
                loadingMap.setMessage("Loading Map... Please wait!");
                break;
            case "vietnamese":
                loadingMap.setTitle("Thông báo");
                loadingMap.setMessage("Đang tải Bản đồ... Vui lòng chờ!");
                break;
        }
        loadingMap.setCanceledOnTouchOutside(false);
        loadingMap.show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public Bitmap resizeMapIcons() {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("museum_red", "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 80, 80, false);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentPosition();

        getMuseumsFromDatabase();

        loadingMap.dismiss();
    }

    private void getCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS
                    );
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(locationChangeListener);
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Settings",null);
        cursor.moveToFirst();
        do {
            language = cursor.getString(3);
        }
        while (cursor.moveToNext());
        cursor.close();
    }

    private void getMuseumsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM Museum order by name", null);
        cursor.moveToFirst();
        do {
            name = cursor.getString(1);
            table = cursor.getString(2);
            image = cursor.getString(3);
            address = cursor.getString(5);
            latitude = cursor.getDouble(6);
            longitude = cursor.getDouble(7);

            String place;
            try {
                // get input stream for text
                InputStream is = getAssets().open(table+"/"+language+"/"+address);
                // check size
                int size = is.available();
                // create buffer for IO
                byte[] buf = new byte[size];
                // get data to buffer
                is.read(buf);
                // close stream
                is.close();
                // set result to TextView
                place = new String(buf);
            }
            catch (IOException ex) {
                return;
            }

            LatLng museum = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(museum)
                    .title(name)
                    .snippet(place)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons()))
            );
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
}
