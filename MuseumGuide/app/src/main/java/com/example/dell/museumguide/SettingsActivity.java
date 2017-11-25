package com.example.dell.museumguide;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.dell.museumguide.R.id.btnBackground_3;
import static com.example.dell.museumguide.R.id.btnBackground_4;

public class SettingsActivity extends AppCompatActivity {

    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    boolean auto;
    boolean dark;
    String background;
    String language;

    RelativeLayout activity_settings;
    ImageView background_settings;
    TextView txtSettings;
    TextView txtBackground;
    TextView txtLanguage;
    Switch swAuto;
    Switch swDark;
    RadioButton btnBackground_1;
    RadioButton btnBackground_2;
    RadioButton btnBackground_3;
    RadioButton btnBackground_4;
    RadioGroup btnBackground;
    RadioButton btnEnglish;
    RadioButton btnVietnamese;
    RadioGroup btnLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        processCopy();
        getSettingsFromDatabase();

        addControls();
    }

    private void addControls() {
        activity_settings = (RelativeLayout) findViewById(R.id.activity_settings);

        txtSettings = (TextView) findViewById(R.id.txtSettings);

        txtBackground = (TextView) findViewById(R.id.txtBackground);

        txtLanguage = (TextView) findViewById(R.id.txtLanguage);

        btnBackground = (RadioGroup) findViewById(R.id.btnBackground);

        btnBackground_1 = (RadioButton) findViewById(R.id.btnBackground_1);
        btnBackground_2 = (RadioButton) findViewById(R.id.btnBackground_2);
        btnBackground_3 = (RadioButton) findViewById(R.id.btnBackground_3);
        btnBackground_4 = (RadioButton) findViewById(R.id.btnBackground_4);
        switch (background){
            case "background/background_1.png":
                btnBackground_1.setChecked(true);
                break;
            case "background/background_2.png":
                btnBackground_2.setChecked(true);
                break;
            case "background/background_3.png":
                btnBackground_3.setChecked(true);
                break;
            case "background/background_4.png":
                btnBackground_4.setChecked(true);
                break;
        }

        try {
            InputStream inputStream = getAssets().open("background/background_1.png");
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            btnBackground_1.setBackground(drawable);

            InputStream inputStream1 = getAssets().open("background/background_2.png");
            Drawable drawable1 = Drawable.createFromStream(inputStream1,null);
            btnBackground_2.setBackground(drawable1);

            InputStream inputStream2 = getAssets().open("background/background_3.png");
            Drawable drawable2 = Drawable.createFromStream(inputStream2,null);
            btnBackground_3.setBackground(drawable2);

            InputStream inputStream3 = getAssets().open("background/background_4.png");
            Drawable drawable3 = Drawable.createFromStream(inputStream3,null);
            btnBackground_4.setBackground(drawable3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        swAuto = (Switch) findViewById(R.id.swAuto);
        if (auto){
            swAuto.setChecked(true);
        }
        else {
            swAuto.setChecked(false);
        }

        swDark = (Switch) findViewById(R.id.swDark);
        if (dark){
            swDark.setChecked(true);

            activity_settings.setBackgroundResource(android.R.color.black);

            txtSettings.setTextColor(Color.WHITE);
            txtBackground.setTextColor(Color.WHITE);
            txtLanguage.setTextColor(Color.WHITE);
            swAuto.setTextColor(Color.WHITE);
            swDark.setTextColor(Color.WHITE);
        }
        else {
            swDark.setChecked(false);

            activity_settings.setBackgroundResource(android.R.color.white);

            txtSettings.setTextColor(Color.BLACK);
            txtBackground.setTextColor(Color.BLACK);
            txtLanguage.setTextColor(Color.BLACK);
            swAuto.setTextColor(Color.BLACK);
            swDark.setTextColor(Color.BLACK);
        }

        btnEnglish = (RadioButton) findViewById(R.id.btnEnglish);
        btnVietnamese = (RadioButton) findViewById(R.id.btnVietnamese);
        btnLanguage = (RadioGroup) findViewById(R.id.btnLanguage);

        switch (language){
            case "english":
                btnEnglish.setChecked(true);

                txtSettings.setText(R.string.text_settings_en);
                txtBackground.setText(R.string.text_background_en);
                txtLanguage.setText(R.string.text_language_en);
                swAuto.setText(R.string.text_auto_en);
                swDark.setText(R.string.text_dark_en);
                break;
            case "vietnamese":
                btnVietnamese.setChecked(true);

                txtSettings.setText(R.string.text_settings_vi);
                txtBackground.setText(R.string.text_background_vi);
                txtLanguage.setText(R.string.text_language_vi);
                swAuto.setText(R.string.text_auto_vi);
                swDark.setText(R.string.text_dark_vi);
                break;
        }

        background_settings = (ImageView) findViewById(R.id.background_settings);

        try {
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_settings.setImageDrawable(drawable);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    public void swAuto_click(View view) {
        int auto_settings;

        if (auto){
            auto = false;
            auto_settings = 0;
        }
        else {
            auto = true;
            auto_settings = 1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("auto",auto_settings);
        database.update("Settings",contentValues,null,null);
    }

    public void swDark_click(View view) {
        int dark_settings;

        if (dark){
            dark = false;
            dark_settings = 0;

            txtSettings.setTextColor(Color.BLACK);
            txtBackground.setTextColor(Color.BLACK);
            txtLanguage.setTextColor(Color.BLACK);
            swAuto.setTextColor(Color.BLACK);
            swDark.setTextColor(Color.BLACK);

            activity_settings.setBackgroundResource(android.R.color.white);
        }
        else {
            dark = true;
            dark_settings = 1;

            txtSettings.setTextColor(Color.WHITE);
            txtBackground.setTextColor(Color.WHITE);
            txtLanguage.setTextColor(Color.WHITE);
            swAuto.setTextColor(Color.WHITE);
            swDark.setTextColor(Color.WHITE);

            activity_settings.setBackgroundResource(android.R.color.black);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("dark",dark_settings);
        database.update("Settings",contentValues,null,null);
    }

    public void btnBackground_click(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.btnBackground_1:
                if (checked){
                    background = "background/background_1.png";
                }
                break;
            case R.id.btnBackground_2:
                if (checked){
                    background = "background/background_2.png";
                }
                break;
            case R.id.btnBackground_3:
                if (checked){
                    background = "background/background_3.png";
                }
                break;
            case R.id.btnBackground_4:
                if (checked){
                    background = "background/background_4.png";
                }
                break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("background",background);
        database.update("Settings",contentValues,null,null);

        try {
            InputStream inputStream = getAssets().open(background);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            background_settings.setImageDrawable(drawable);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnLanguage_click(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.btnEnglish:
                if (checked){
                    language = "english";

                    txtSettings.setText(R.string.text_settings_en);
                    txtBackground.setText(R.string.text_background_en);
                    txtLanguage.setText(R.string.text_language_en);
                    swAuto.setText(R.string.text_auto_en);
                    swDark.setText(R.string.text_dark_en);
                }
                break;
            case R.id.btnVietnamese:
                if (checked){
                    language = "vietnamese";

                    txtSettings.setText(R.string.text_settings_vi);
                    txtBackground.setText(R.string.text_background_vi);
                    txtLanguage.setText(R.string.text_language_vi);
                    swAuto.setText(R.string.text_auto_vi);
                    swDark.setText(R.string.text_dark_vi);
                }
                break;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("language",language);
        database.update("Settings",contentValues,null,null);
    }
}
