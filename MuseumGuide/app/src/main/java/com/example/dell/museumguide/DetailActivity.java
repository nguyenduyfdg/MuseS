package com.example.dell.museumguide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.dell.adapter.DarkArtifactAdapter;
import com.example.dell.adapter.LightArtifactAdapter;
import com.example.dell.model.ArtifactView;
import com.example.dell.model.BeaconView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, BeaconConsumer {

    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager = null;

    RelativeLayout activity_detail;
    ImageView background_detail;
    TabHost tabHost;

    TextView txtMuseumName;

    TextView txtOverview;
    TextView txtOverviewContent;

    TextView txtAddress;
    TextView txtAddressContent;

    TextView txtContract;
    TextView txtContractContent;
    TextView txtContractValue;

    TextView txtTime;
    TextView txtTimeContent;
    TextView txtTimeValue;

    TextView txtTicket;
    TextView txtTicketContent;
    TextView txtTicketValue;

    TextView txtArtifactList;
    ListView lvArtifact;
    ArrayList<ArtifactView> arrArtifact;
    DarkArtifactAdapter darkArtifactAdapter;
    LightArtifactAdapter lightArtifactAdapter;

    boolean auto;
    boolean dark;
    String language = "";

    int id;
    String title = "";
    String image = "";
    String address = "";

    ArrayList<String> addressList = new ArrayList<>();
    String beacon = "";
    int maxRSSI;
    ArrayList<BeaconView> beaconList = new ArrayList<>();

    String path = "";
    String background = "";
    String place = "";
    String name = "";
    String overview = "";
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        background = intent.getStringExtra("background");
        place = intent.getStringExtra("place");
        name = intent.getStringExtra("name");
        overview = intent.getStringExtra("overview");
        latitude = intent.getDoubleExtra("latitude",0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);

        processCopy();
        getSettingsFromDatabase();

        addControls();
        addEvents();

        getArtifactsFromDatabase();
        getContractFromDatabase();
        getTimeFromDatabase();
        getTicketFromDatabase();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auto){
            beaconConfigure();

            if (!beaconManager.isBound(this)){
                beaconManager.bind(this);
            }
        }
    }

    private void beaconConfigure() {
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());

        // Detect the iBeacons:
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    private void addEvents() {
        lvArtifact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtifactView item = (ArtifactView) parent.getItemAtPosition(position);
                int x = item.getId();

                Intent intent = new Intent(DetailActivity.this, MediaActivity.class);

                intent.putExtra("path",path);
                intent.putExtra("id",x);

                startActivity(intent);
            }
        });
    }

    private void addControls() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabOverview = tabHost.newTabSpec("tabOverview");
        tabOverview.setContent(R.id.tabOverview);

        TabHost.TabSpec tabMap = tabHost.newTabSpec("tabMap");
        tabMap.setContent(R.id.tabMap);

        TabHost.TabSpec tabArtifacts = tabHost.newTabSpec("tabArtifacts");
        tabArtifacts.setContent(R.id.tabArtifacts);

        Drawable drawable = null;
        try {
            InputStream inputStream = getAssets().open(background);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        activity_detail = (RelativeLayout) findViewById(R.id.activity_detail);

        background_detail = (ImageView) findViewById(R.id.background_detail);
        background_detail.setImageDrawable(drawable);

        tabHost = (TabHost) findViewById(R.id.tabHost);

        txtMuseumName = (TextView) findViewById(R.id.txtMuseumName);
        txtMuseumName.setText(name);

        txtOverview = (TextView) findViewById(R.id.txtOverview);
        txtOverviewContent = (TextView) findViewById(R.id.txtOverviewContent);
        txtOverviewContent.setText(overview);

        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtAddressContent = (TextView) findViewById(R.id.txtAddressContent);
        txtAddressContent.setText(place);

        txtContract = (TextView) findViewById(R.id.txtContract);
        txtContractContent = (TextView) findViewById(R.id.txtContractContent);
        txtContractValue = (TextView) findViewById(R.id.txtContractValue);

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTimeContent = (TextView) findViewById(R.id.txtTimeContent);
        txtTimeValue = (TextView) findViewById(R.id.txtTimeValue);

        txtTicket = (TextView) findViewById(R.id.txtTicket);
        txtTicketContent = (TextView) findViewById(R.id.txtTicketContent);
        txtTicketValue = (TextView) findViewById(R.id.txtTicketValue);

        txtArtifactList = (TextView) findViewById(R.id.txtArtifactList);

        arrArtifact = new ArrayList<>();

        darkArtifactAdapter = new DarkArtifactAdapter(
                DetailActivity.this,
                R.layout.list_artifact,
                arrArtifact
        );

        lightArtifactAdapter = new LightArtifactAdapter(
                DetailActivity.this,
                R.layout.list_artifact,
                arrArtifact
        );

        lvArtifact = (ListView) findViewById(R.id.lvArtifact);

        if (dark){
            lvArtifact.setAdapter(darkArtifactAdapter);

            txtArtifactList.setTextColor(Color.WHITE);

            txtMuseumName.setTextColor(Color.WHITE);

            txtOverview.setTextColor(Color.WHITE);
            txtOverviewContent.setTextColor(Color.WHITE);

            txtAddress.setTextColor(Color.WHITE);
            txtAddressContent.setTextColor(Color.WHITE);

            txtContract.setTextColor(Color.WHITE);
            txtContractContent.setTextColor(Color.WHITE);
            txtContractValue.setTextColor(Color.WHITE);

            txtTime.setTextColor(Color.WHITE);
            txtTimeContent.setTextColor(Color.WHITE);
            txtTimeValue.setTextColor(Color.WHITE);

            txtTicket.setTextColor(Color.WHITE);
            txtTicketContent.setTextColor(Color.WHITE);
            txtTicketValue.setTextColor(Color.WHITE);

            activity_detail.setBackgroundResource(android.R.color.black);
        }
        else {
            lvArtifact.setAdapter(lightArtifactAdapter);

            txtArtifactList.setTextColor(Color.BLACK);

            txtMuseumName.setTextColor(Color.BLACK);

            txtOverview.setTextColor(Color.BLACK);
            txtOverviewContent.setTextColor(Color.BLACK);

            txtAddress.setTextColor(Color.BLACK);
            txtAddressContent.setTextColor(Color.BLACK);

            txtContract.setTextColor(Color.BLACK);
            txtContractContent.setTextColor(Color.BLACK);
            txtContractValue.setTextColor(Color.BLACK);

            txtTime.setTextColor(Color.BLACK);
            txtTimeContent.setTextColor(Color.BLACK);
            txtTimeValue.setTextColor(Color.BLACK);

            txtTicket.setTextColor(Color.BLACK);
            txtTicketContent.setTextColor(Color.BLACK);
            txtTicketValue.setTextColor(Color.BLACK);

            activity_detail.setBackgroundResource(android.R.color.white);
        }

        switch (language){
            case "english":
                txtArtifactList.setText(R.string.text_artifact_list_en);
                txtOverview.setText(R.string.text_overview_en);
                txtAddress.setText(R.string.text_address_en);
                txtContract.setText(R.string.text_contract_en);
                txtTime.setText(R.string.text_time_en);
                txtTicket.setText(R.string.text_ticket_en);

                tabOverview.setIndicator("Overview");
                tabMap.setIndicator("Map");
                tabArtifacts.setIndicator("Artifacts");

                break;
            case "vietnamese":
                txtArtifactList.setText(R.string.text_artifact_list_vi);
                txtOverview.setText(R.string.text_overview_vi);
                txtAddress.setText(R.string.text_address_vi);
                txtContract.setText(R.string.text_contract_vi);
                txtTime.setText(R.string.text_time_vi);
                txtTicket.setText(R.string.text_ticket_vi);

                tabOverview.setIndicator("Tổng quan");
                tabMap.setIndicator("Bản đồ");
                tabArtifacts.setIndicator("Hiện vật");

                break;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMuseum);
        mapFragment.getMapAsync(this);

        tabHost.addTab(tabOverview);
        tabHost.addTab(tabMap);
        tabHost.addTab(tabArtifacts);
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Settings",null);
        cursor.moveToFirst();
        do {
            auto = cursor.getInt(0) != 0;
            dark = cursor.getInt(1) != 0;
            language = cursor.getString(3);
        }
        while (cursor.moveToNext());
        cursor.close();
    }

    private void getArtifactsFromDatabase() {
        arrArtifact.clear();

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " +path+ " order by title", null);
        cursor.moveToFirst();
        do {
            id = cursor.getInt(0);
            title = cursor.getString(1);
            image = cursor.getString(3);
            address = cursor.getString(6);

            addressList.add(address);

            arrArtifact.add(new ArtifactView(id,title,image,address));
        }
        while (cursor.moveToNext());
        cursor.close();
    }

    private void getContractFromDatabase() {
        String contractContent = null;
        String contractValue = null;

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " +path+ "_contract", null);
        cursor.moveToFirst();
        do {
            switch (language){
                case "english":
                    contractContent += cursor.getString(1) + "\n";
                    break;
                case "vietnamese":
                    contractContent += cursor.getString(2) + "\n";
            }
            contractValue += cursor.getString(0) + "\n";
        }
        while (cursor.moveToNext());
        cursor.close();

        assert contractContent != null;
        contractContent = contractContent.substring(4,contractContent.length()-1);
        contractValue = contractValue.substring(4,contractValue.length()-1);

        txtContractContent.setText(contractContent);
        txtContractValue.setText(contractValue);
    }

    private void getTicketFromDatabase() {
        String ticketContent = null;
        String ticketValue = null;

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " +path+ "_ticket", null);
        cursor.moveToFirst();
        do {
            switch (language){
                case "english":
                    ticketContent += cursor.getString(1) + "\n";
                    break;
                case "vietnamese":
                    ticketContent += cursor.getString(2) + "\n";
            }
            ticketValue += cursor.getString(0) + "\n";
        }
        while (cursor.moveToNext());
        cursor.close();

        assert ticketContent != null;
        ticketContent = ticketContent.substring(4,ticketContent.length()-1);
        ticketValue = ticketValue.substring(4,ticketValue.length()-1);

        txtTicketContent.setText(ticketContent);
        txtTicketValue.setText(ticketValue);
    }

    private void getTimeFromDatabase() {
        String timeContent = null;
        String timeValue = null;

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " +path+ "_time", null);
        cursor.moveToFirst();
        do {
            switch (language){
                case "english":
                    timeContent += cursor.getString(1) + "\n";
                    break;
                case "vietnamese":
                    timeContent += cursor.getString(2) + "\n";
            }
            timeValue += cursor.getString(0) + "\n";
        }
        while (cursor.moveToNext());
        cursor.close();

        assert timeContent != null;
        timeContent = timeContent.substring(4,timeContent.length()-1);
        timeValue = timeValue.substring(4,timeValue.length()-1);

        txtTimeContent.setText(timeContent);
        txtTimeValue.setText(timeValue);
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

    public Bitmap resizeMapIcons() {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("museum_red", "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 80, 80, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng museum = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(museum)
                .title(name)
                .snippet(place)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons()))
        );

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(museum, (float) 17));
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("myBeacons", null, null, null);
        Log.i(TAG,"onBeaconServiceConnect");

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.i(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(TAG,"notEnterRegion");
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.i(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(TAG,"notExitRegion");
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG,"didDetermineStateForRegion");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                    Log.i(TAG,"startRangingBeaconsInRegion");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                int x = -1;
                Log.i(TAG,"didRangeBeaconsInRegion " + beacons.size());
                if (beacons.size() > 0){
                    for (Beacon oneBeacon : beacons) {
                        beaconList.add(new BeaconView(
                                oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3(),
                                oneBeacon.getRssi()
                                )
                        );
                        Log.i(
                                TAG,
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

                    if (beaconList.size() >= 6) {
                        maxRSSI = beaconList.get(0).getRssi();
                        beacon = beaconList.get(0).getAddress();
                        for (BeaconView beaconView : beaconList) {
                            if (beaconView.getRssi() > maxRSSI) {
                                beacon = beaconView.getAddress();
                                maxRSSI = beaconView.getRssi();
                            }
                        }
                        Log.i(TAG, "Max RSSI: " + maxRSSI + " ID: " + beacon);

                        for (int i = 0; i < addressList.size(); i++){
                            Log.i(TAG,addressList.get(i));
                            if (beacon.equals(addressList.get(i))){
                                x = arrArtifact.get(i).getId();
                            }
                        }

                        if (x > -1){
                            beaconList.clear();

                            beaconManager.unbind(DetailActivity.this);
                            beaconManager.removeAllMonitorNotifiers();
                            beaconManager.removeAllRangeNotifiers();

                            Intent intent = new Intent(DetailActivity.this,MediaActivity.class);

                            intent.putExtra("path", path);
                            intent.putExtra("id", x);

                            startActivity(intent);
                        }

                        beaconList.clear();
                    }
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
            Log.i(TAG,"startMonitoringBeaconsInRegion");
        } catch (RemoteException e) {
            Log.i(TAG,"stopMonitoringBeaconsInRegion");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (auto){
            if (beaconManager.isBound(this)){
                beaconManager.unbind(this);
                beaconManager.removeAllMonitorNotifiers();
                beaconManager.removeAllRangeNotifiers();
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (auto){
            if (beaconManager.isBound(this)){
                beaconManager.unbind(this);
                beaconManager.removeAllMonitorNotifiers();
                beaconManager.removeAllRangeNotifiers();
            }
        }

        super.onDestroy();
    }
}