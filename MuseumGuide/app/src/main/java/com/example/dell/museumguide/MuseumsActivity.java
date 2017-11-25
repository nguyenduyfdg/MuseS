package com.example.dell.museumguide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.adapter.DarkMuseumAdapter;
import com.example.dell.adapter.LightMuseumAdapter;
import com.example.dell.model.MuseumView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MuseumsActivity extends AppCompatActivity {

    ListView lvMuseum;
    ArrayList<MuseumView> arrMuseum;
    DarkMuseumAdapter darkMuseumAdapter;
    LightMuseumAdapter lightMuseumAdapter;


    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    boolean auto;
    boolean dark;
    String background;
    String language;

    int id;
    String name;
    String image;
    String table;
    String content;
    String address;
    double latitude;
    double longitude;

    RelativeLayout activity_museums;
    ImageView background_museum;
    TextView txtMuseumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museums);
    }

    @Override
    protected void onResume() {
        super.onResume();

        processCopy();
        getSettingsFromDatabase();

        addControls();
        addEvents();

        getMuseumsFromDatabase();
    }

    private void addEvents() {
        lvMuseum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MuseumView item = (MuseumView) parent.getItemAtPosition(position);
                String path = item.getPath();
                String image = item.getImage();
                String place = item.getAddress();
                String name = item.getName();
                String overview = item.getContent();
                double latitude = item.getLatitude();
                double longitude = item.getLongitude();

                Intent intent = new Intent(MuseumsActivity.this,DetailActivity.class);

                intent.putExtra("path",path);
                intent.putExtra("background",image);
                intent.putExtra("place",place);
                intent.putExtra("name",name);
                intent.putExtra("overview",overview);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);

                startActivity(intent);
            }
        });
    }

    private void addControls() {
        txtMuseumList = (TextView) findViewById(R.id.txtMuseumList);
        activity_museums = (RelativeLayout) findViewById(R.id.activity_museums);
        background_museum = (ImageView) findViewById(R.id.background_museum);

        arrMuseum = new ArrayList<>();

        darkMuseumAdapter = new DarkMuseumAdapter(
                MuseumsActivity.this,
                R.layout.list_museum,
                arrMuseum
        );

        lightMuseumAdapter = new LightMuseumAdapter(
                MuseumsActivity.this,
                R.layout.list_museum,
                arrMuseum
        );

        lvMuseum = (ListView) findViewById(R.id.lvMuseum);

        if (dark){
            lvMuseum.setAdapter(darkMuseumAdapter);
            txtMuseumList.setTextColor(Color.WHITE);
            activity_museums.setBackgroundResource(android.R.color.black);
        }
        else {
            lvMuseum.setAdapter(lightMuseumAdapter);
            txtMuseumList.setTextColor(Color.BLACK);
            activity_museums.setBackgroundResource(android.R.color.white);
        }

        switch (language){
            case "english":
                txtMuseumList.setText(R.string.text_museum_list_en);
                break;
            case "vietnamese":
                txtMuseumList.setText(R.string.text_museum_list_vi);
                break;
        }

        try {
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_museum.setImageDrawable(drawable);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from Settings",null);
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

    private void getMuseumsFromDatabase() {
        arrMuseum.clear();

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

            String place;
            String overview;
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

                InputStream inputStream = getAssets().open(table+"/"+language+"/"+content);
                int size1 = inputStream.available();
                byte[] buf1 = new byte[size1];
                inputStream.read(buf1);
                inputStream.close();
                overview = new String(buf1);
            }
            catch (IOException ex) {
                return;
            }

            arrMuseum.add(new MuseumView(id, image, name, place, table, overview, latitude, longitude));
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

    private String getDatabasePath(){
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
}
