package com.example.dell.museumguide;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothCheckerActivity extends AppCompatActivity {

    String DATABASE_NAME="dbMuseums.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;

    String language;

    TextView txtBluetoothChecker;
    Button btnSettingsBluetooth;
    Button btnCloseBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_checker);

        processCopy();
        getSettingsFromDatabase();

        addControls();
    }

    private void addControls() {
        txtBluetoothChecker = (TextView) findViewById(R.id.txtBluetoothChecker);
        btnSettingsBluetooth = (Button) findViewById(R.id.btnSettingsBluetooth);
        btnCloseBluetooth = (Button) findViewById(R.id.btnCloseBluetooth);

        switch (language){
            case "english":
                txtBluetoothChecker.setText("Bluetooth settings is currently off. Please turn on your bluetooth connection to fully use this app.");
                btnSettingsBluetooth.setText(R.string.text_settings_en);
                btnCloseBluetooth.setText(R.string.text_close_en);
                break;
            case "vietnamese":
                txtBluetoothChecker.setText("Bluetooth của bạn hiện đang tắt. Bật kết nối bluetooth của bạn để có thể sử dụng ứng dụng này một cách tốt nhất.");
                btnSettingsBluetooth.setText(R.string.text_settings_vi);
                btnCloseBluetooth.setText(R.string.text_close_vi);
                break;
        }
    }

    public void btnSettingsBluetooth_click(View view) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
        finish();
    }

    public void btnCloseBluetooth_click(View view) {
        finish();
    }

    private void getSettingsFromDatabase() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM Settings", null);
        cursor.moveToFirst();
        do {
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
}
