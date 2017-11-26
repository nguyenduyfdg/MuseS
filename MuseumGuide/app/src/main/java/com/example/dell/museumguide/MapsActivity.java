package com.example.dell.museumguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.model.JSONParser;
import com.example.dell.model.MuseumView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String PERMISSION_TAG = "PERMISSIONS";
    private static final String LOCATION_TAG = "LOCATION";
    private static final String ERROR_TAG = "ERROR";

    String DATABASE_NAME = "dbMuseums_1.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    
    FloatingActionButton fabMaps;
    LinearLayout layoutRoute;
    TextView txtRoute;
    LinearLayout layoutSearch;
    TextView txtSearch;

    private Animation fabMenuOpenAnimation;
    private Animation fabMenuCloseAnimation;
    private Animation fabOpenAnimation;
    private Animation fabCloseAnimation;
    private boolean isFabMenuOpen = false;

    int id;
    String name = "";
    String image = "";
    String table = "";
    String content = "";
    String address = "";
    double latitude;
    double longitude;

    TextView txtSearchDialog;
    AutoCompleteTextView txtMuseumNameDialog;
    ArrayList<String> museumNameList = new ArrayList<>();
    ArrayAdapter<String> museumNameAdapter;

    String language = "";
    boolean auto;
    boolean autoOpen = true;
    boolean showingDialog;
    double autoLat;
    double autoLng;

    ArrayList<MuseumView> museumsList = new ArrayList<>();
    LatLng source;
    LatLng destination;
    ConnectAsyncTask connectAsyncTask;
    boolean doAsyncTask;
    ArrayList<Polyline> linesList = new ArrayList<>();

    ProgressDialog loadingMap;
    boolean firstOpen = true;
    private GoogleMap mMap;
    GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            source = new LatLng((location.getLatitude()), location.getLongitude());

            if (firstOpen) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 16.5f));
                firstOpen = false;
            }

            if (auto) {
                if (autoOpen) {
                    if (!showingDialog) {
                        for (MuseumView museum : museumsList) {
                            double distance = getDistance(
                                    source.latitude,
                                    source.longitude,
                                    museum.getLatitude(),
                                    museum.getLongitude()
                            );
                            if (distance <= 0.02) {
                                final String path = museum.getPath();
                                final String image = museum.getImage();
                                final String place = museum.getAddress();
                                final String name = museum.getName();
                                final String overview = museum.getContent();
                                autoLat = museum.getLatitude();
                                autoLng = museum.getLongitude();

                                String message = "";
                                String positiveButton = "";
                                String negativeButton = "";
                                AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this).create();
                                switch (language) {
                                    case "english":
                                        message = "We detected you are now at " + name + ". Do you want to see detail about it?";
                                        positiveButton = "OK";
                                        negativeButton = "NO";
                                        break;
                                    case "vietnamese":
                                        message = "Chúng tôi phát hiện bạn đang ở " + name + ". Bạn có muốn xem chi tiết về nó không?";
                                        positiveButton = "Đồng ý";
                                        negativeButton = "Từ chối";
                                        break;
                                }
                                dialog.setMessage(message);
                                dialog.setCancelable(false);
                                dialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveButton, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MapsActivity.this, DetailActivity.class);

                                        intent.putExtra("path",path);
                                        intent.putExtra("background",image);
                                        intent.putExtra("place",place);
                                        intent.putExtra("name",name);
                                        intent.putExtra("overview",overview);
                                        intent.putExtra("latitude",autoLat);
                                        intent.putExtra("longitude",autoLng);

                                        startActivity(intent);
                                    }
                                });
                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeButton, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        autoOpen = false;
                                        showingDialog = false;
                                    }
                                });
                                dialog.show();
                                showingDialog = true;
                            }
                        }
                    }
                }
                else {
                    double distance = getDistance(source.latitude, source.longitude, autoLat, autoLng);
                    if (distance > 0.02) {
                        autoOpen = true;
                    }
                }
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
        return Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentPosition();
        getMuseumsFromDatabase();
        
        addControls();
        addEvents();

        loadingMap.dismiss();
    }

    private void addEvents() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }

                destination = new LatLng(
                        marker.getPosition().latitude,
                        marker.getPosition().longitude
                );

                return false;
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }

                if (linesList.size() > 0) {
                    for (Polyline lines : linesList) {
                        if (lines.getId().equals(polyline.getId())) {
                            lines.setColor(Color.BLUE);
                        }
                        else {
                            lines.remove();
                        }
                    }
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (MuseumView museum : museumsList) {
                    if (marker.getPosition().latitude == museum.getLatitude() && marker.getPosition().longitude == museum.getLongitude()) {
                        String path = museum.getPath();
                        String image = museum.getImage();
                        String place = museum.getAddress();
                        String name = museum.getName();
                        String overview = museum.getContent();
                        double latitude = museum.getLatitude();
                        double longitude = museum.getLongitude();

                        Intent intent = new Intent(MapsActivity.this, DetailActivity.class);

                        intent.putExtra("path",path);
                        intent.putExtra("background",image);
                        intent.putExtra("place",place);
                        intent.putExtra("name",name);
                        intent.putExtra("overview",overview);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);

                        startActivity(intent);
                    }
                }
            }
        });

        fabMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }
                else {
                    expandFabMenu();
                    isFabMenuOpen = true;
                }
            }
        });

        layoutRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }

                if (source != null && destination != null){
                    Log.i(LOCATION_TAG, Double.toString(source.latitude));
                    Log.i(LOCATION_TAG, Double.toString(source.longitude));
                    Log.i(LOCATION_TAG, Double.toString(destination.latitude));
                    Log.i(LOCATION_TAG, Double.toString(destination.longitude));

                    String url = makeURL(source.latitude, source.longitude, destination.latitude, destination.longitude);
                    doAsyncTask = true;
                    connectAsyncTask = new ConnectAsyncTask(url);
                    connectAsyncTask.execute();
                }
            }
        });

        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabMenuOpen) {
                    collapseFabMenu();
                    isFabMenuOpen = false;
                }

                LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
                @SuppressLint("InflateParams") View searchDialogView = inflater.inflate(R.layout.search_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                alertDialogBuilder.setView(searchDialogView);

                txtSearchDialog = (TextView) searchDialogView.findViewById(R.id.txtSearchDialog);
                switch (language) {
                    case "english":
                        txtSearchDialog.setText(R.string.search_dialog_en);
                        break;
                    case "vietnamese":
                        txtSearchDialog.setText(R.string.search_dialog_vi);
                }

                txtMuseumNameDialog = (AutoCompleteTextView) searchDialogView.findViewById(R.id.txtMuseumNameDialog);
                txtMuseumNameDialog.setAdapter(museumNameAdapter);

                alertDialogBuilder.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String museumName = txtMuseumNameDialog.getText().toString();

                        for (MuseumView museum : museumsList) {
                            if (museumName.equals(museum.getName())) {
                                destination = new LatLng(museum.getLatitude(), museum.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 16.5f));
                            }
                        }
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void addControls() {
        fabMaps = (FloatingActionButton) findViewById(R.id.fabMaps);

        layoutRoute = (LinearLayout) findViewById(R.id.layoutRoute);
        txtRoute = (TextView) findViewById(R.id.txtRoute);

        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        txtSearch = (TextView) findViewById(R.id.txtSearch);

        switch (language) {
            case "english":
                txtRoute.setText(R.string.route_maps_en);
                txtSearch.setText(R.string.search_maps_en);
                break;
            case "vietnamese":
                txtRoute.setText(R.string.route_maps_vi);
                txtSearch.setText(R.string.search_maps_vi);
                break;
        }

        museumNameAdapter = new ArrayAdapter<String>(
                MapsActivity.this,
                android.R.layout.simple_list_item_1,
                museumNameList
        );

        fabMenuCloseAnimation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fab_closing_menu);
        fabMenuOpenAnimation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fab_openning_menu);
        fabCloseAnimation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fab_closing);
        fabOpenAnimation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fab_openning);
    }

    private void expandFabMenu() {
        layoutRoute.startAnimation(fabMenuOpenAnimation);
        layoutRoute.setVisibility(View.VISIBLE);
        layoutRoute.setClickable(true);

        layoutSearch.startAnimation(fabMenuOpenAnimation);
        layoutSearch.setVisibility(View.VISIBLE);
        layoutSearch.setClickable(true);

        fabMaps.startAnimation(fabOpenAnimation);
    }

    private void collapseFabMenu() {
        layoutRoute.startAnimation(fabMenuCloseAnimation);
        layoutRoute.setVisibility(View.INVISIBLE);
        layoutRoute.setClickable(false);

        layoutSearch.startAnimation(fabMenuCloseAnimation);
        layoutSearch.setVisibility(View.INVISIBLE);
        layoutSearch.setClickable(false);

        fabMaps.startAnimation(fabCloseAnimation);
    }

    private void getCurrentPosition() {
        int buildVer = Build.VERSION.SDK_INT;
        if (buildVer >= 23){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.i(PERMISSION_TAG, "ACCESS_FINE_LOCATION granted");
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                Log.i(PERMISSION_TAG, "ACCESS_FINE_LOCATION revoke");
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(locationChangeListener);
        }
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Settings",null);
        cursor.moveToFirst();
        do {
            auto = cursor.getInt(0) != 0;
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
            id = cursor.getInt(0);
            name = cursor.getString(1);
            table = cursor.getString(2);
            image = cursor.getString(3);
            content = cursor.getString(4);
            address = cursor.getString(5);
            latitude = cursor.getDouble(6);
            longitude = cursor.getDouble(7);

            museumNameList.add(name);

            String place = null;
            String overview = null;
            try {
                // get input stream for text
                InputStream is = getAssets().open(table + "/overview/" + language + address);
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

                InputStream inputStream = getAssets().open(table + "/overview/" + language + content);
                int size1 = inputStream.available();
                byte[] buf1 = new byte[size1];
                inputStream.read(buf1);
                inputStream.close();
                overview = new String(buf1);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name)
                    .snippet(place)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons()))
            );

            museumsList.add(
                    new MuseumView(
                            id,
                            table + "/overview/" + image,
                            name,
                            place,
                            table,
                            overview,
                            latitude,
                            longitude
                    )
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

    public String makeURL (double sourceLat, double sourceLog, double destLat, double destLog ){
        return "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" +// from
                Double.toString(sourceLat) +
                "," +
                Double.toString(sourceLog) +
                "&destination=" +// to
                Double.toString(destLat) +
                "," +
                Double.toString(destLog) +
                "&sensor=false&mode=driving&alternatives=true" +
                "&key=AIzaSyCWGdPP6chjRgAVmYKdZsv1IN8JC7P1Hu0";
    }

    public void drawPath(String  result) {
        try {
            if (linesList.size() > 0) {
                for (Polyline polyline : linesList) {
                    polyline.remove();
                }
                linesList.clear();
            }

            //Transform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");

            for (int i = 0; i < routeArray.length(); i++) {
                JSONObject routes = routeArray.getJSONObject(i);
                JSONObject overviewPolyline = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolyline.getString("points");
                List<LatLng> list = decodePoly(encodedString);

                PolylineOptions options = new PolylineOptions()
                        .width(18)
                        .color(Color.LTGRAY)
                        .geodesic(true)
                        .clickable(true);
                for (int z = 0; z < list.size(); z++) {
                    LatLng point = list.get(z); options.add(point);
                }

                linesList.add(mMap.addPolyline(options));
            }

            if (linesList.size() == 1) {
                linesList.get(0).setColor(Color.BLUE);
            }
        }
        catch (JSONException e) {
            Log.i(ERROR_TAG, e.getMessage());
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;
        ConnectAsyncTask(String urlPass){
            url = urlPass;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            String json = null;
            if (doAsyncTask) {
                JSONParser jParser = new JSONParser();
                json = jParser.getJSONFromUrl(url);
            }
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(LOCATION_TAG, result);

            progressDialog.dismiss();
            doAsyncTask = false;
            if(result != null) {
                drawPath(result);
            }
        }
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onBackPressed() {
        if (isFabMenuOpen) {
            collapseFabMenu();
            isFabMenuOpen = false;
        }
        else {
            doAsyncTask = false;
            if (connectAsyncTask != null){
                connectAsyncTask.cancel(true);
            }

            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}