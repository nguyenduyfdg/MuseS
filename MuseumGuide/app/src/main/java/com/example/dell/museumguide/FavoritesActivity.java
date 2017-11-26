package com.example.dell.museumguide;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.adapter.DarkFavoriteAdapter;
import com.example.dell.adapter.LightFavoriteAdapter;
import com.example.dell.model.ArtifactView;
import com.example.dell.model.FavoriteMuseumView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    SQLiteDatabase database1=null;

    boolean auto;
    boolean dark;
    String background;
    String language;

    int idMuseum;
    String name;
    String imageMuseum;
    String table;
    String addressMuseum;

    int idArtifact;
    String title;
    String imageArtifact;
    String addressArtifact;
    boolean favorite;

    Context context = this;

    ExpandableListView lvFavorites;
    ArrayList<FavoriteMuseumView> arrFavorite;
    DarkFavoriteAdapter darkFavoriteAdapter;
    LightFavoriteAdapter lightFavoriteAdapter;
    ArrayList<ArtifactView> arrArtifact;

    RelativeLayout activity_favorites;
    ImageView background_favorites;
    TextView txtFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
    }

    @Override
    protected void onResume() {
        super.onResume();

        processCopy();
        getSettingsFromDatabase();

        addControls();
        addEvents();

        getDataFromDatabase();
    }

    private void addEvents() {
        lvFavorites.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            // Keep track of previous expanded parent
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    lvFavorites.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        lvFavorites.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FavoriteMuseumView item = (FavoriteMuseumView) parent.getItemAtPosition(groupPosition);
                String path = item.getPath();
                int idArtifact = item.getArtifactViews().get(childPosition).getId();

                Intent intent = new Intent(FavoritesActivity.this, MediaActivity.class);

                intent.putExtra("path",path);
                intent.putExtra("id",idArtifact);

                startActivity(intent);

                return false;
            }
        });
    }

    private void addControls() {
        lvFavorites = (ExpandableListView) findViewById(R.id.lvFavorites);
        arrFavorite = new ArrayList<>();
        darkFavoriteAdapter = new DarkFavoriteAdapter(context,arrFavorite);
        lightFavoriteAdapter = new LightFavoriteAdapter(context,arrFavorite);

        activity_favorites = (RelativeLayout) findViewById(R.id.activity_favorites);
        background_favorites = (ImageView) findViewById(R.id.background_favorites);
        txtFavorites = (TextView) findViewById(R.id.txtFavorites);

        if (dark){
            activity_favorites.setBackgroundResource(android.R.color.black);
            txtFavorites.setTextColor(Color.WHITE);
            lvFavorites.setAdapter(darkFavoriteAdapter);
        }
        else {
            activity_favorites.setBackgroundResource(android.R.color.white);
            txtFavorites.setTextColor(Color.BLACK);
            lvFavorites.setAdapter(lightFavoriteAdapter);
        }

        switch (language){
            case "english":
                txtFavorites.setText(R.string.favorites_list_favorites_en);
                break;
            case "vietnamese":
                txtFavorites.setText(R.string.favorites_list_favorites_vi);
                break;
        }

        try {
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_favorites.setImageDrawable(drawable);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromDatabase() {
        arrFavorite.clear();

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM Museum order by name", null);
        cursor.moveToFirst();
        do {
            idMuseum = cursor.getInt(0);
            name = cursor.getString(1);
            table = cursor.getString(2);
            imageMuseum = cursor.getString(3);
            addressMuseum = cursor.getString(5);

            String place;
            try {
                // get input stream for text
                InputStream is = getAssets().open(table+"/"+language+"/"+addressMuseum);
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

            arrArtifact = new ArrayList<>();
            arrArtifact.clear();
            database1 = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
            Cursor cursor1 = database1.rawQuery("SELECT * FROM " + table + " order by title", null);
            cursor1.moveToFirst();
            do {
                idArtifact = cursor1.getInt(0);
                title = cursor1.getString(1);
                imageArtifact = cursor1.getString(3);
                addressArtifact = cursor1.getString(6);
                favorite = cursor1.getInt(7) > 0;

                if (favorite){
                    arrArtifact.add(new ArtifactView(idArtifact,title,imageArtifact,addressArtifact));
                }
            }
            while (cursor1.moveToNext());
            cursor1.close();
            database1.close();

            if (arrArtifact.size() > 0){
                arrFavorite.add(new FavoriteMuseumView(idMuseum,imageMuseum,name,place,table,arrArtifact));
            }
        }
        while (cursor.moveToNext());
        cursor.close();
        database.close();
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
        database.close();
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
