package org.hse.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor light;
    private TextView sensorLight;

    private ListView listView;

    private EditText name;
    private PreferenceManager preferenceManager;

    private ImageView photo;
    private Button buttonSave;
    private Button buttonAddPhoto;
    private File imageFilePath;
    private File imageFile;
    private Uri photoURI;
    private final Integer REQUEST_IMAGE_CAPTURE = 0;
    private final Integer REQUEST_PERMISSION_CODE = 1;
    private final String[] PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        name = findViewById(R.id.name);
        sensorLight = findViewById(R.id.sensor_light);
        listView = findViewById(R.id.list_view);
        photo = findViewById(R.id.photo);
        buttonAddPhoto = findViewById(R.id.button_add_photo);
        buttonSave = findViewById(R.id.button_save);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        
        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

        List<String> listSensorType = new ArrayList<>();
        for (int i = 0; i < listSensor.size(); i++) {
            listSensorType.add(listSensor.get(i).getName());
        }

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listSensorType);
        listView.setAdapter(adapter);

        preferenceManager = new PreferenceManager(this);
        name.setText(preferenceManager.getValue("name", ""));
        name.setSelection(name.length());

        File imgFile = new File(preferenceManager.getValue("photo", ""));

        if(imgFile.exists())
        {
            Glide.with(this).load(imgFile).into(photo);
        }
        else{
            photo.setImageResource(R.drawable.dzhek);
        }

//        if(imageViewURI != null){
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    photo.setImageURI(photoURI);
//                    photo.invalidate();
//                }
//            });
//        }

        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.saveValue("name", name.getText().toString());
                preferenceManager.saveValue("photo", imageFile.getPath());
                Toast.makeText(SettingsActivity.this, R.string.photo_saved_string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkPermission(){
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION[0]);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            dispatchPictureIntent();
        } else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION[0])){
               showExplanation(getString(R.string.should_permit_title_string), getString(R.string.should_permit_text_string), PERMISSION[0], REQUEST_PERMISSION_CODE);
            }
            else{
                requestPermissions(PERMISSION, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[] {permission}, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] result){
        if (requestCode == REQUEST_PERMISSION_CODE){
            if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED){
                dispatchPictureIntent();
            }
        }
    }

    private void dispatchPictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;

            try{
                photoFile = createImageFile();
            } catch(Exception ex){
                Log.e("tag", "Create file: ", ex);
            }

           if(photoFile != null){
                photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                try{
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch(ActivityNotFoundException ex){
                    Log.e("tag", "Start activity: ", ex);
                }
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "TMG " + timeStamp + " ";

        imageFilePath = new File(getFilesDir(), "external_files");
        imageFilePath.mkdir();
        imageFile = new File(imageFilePath.getPath(), String.format("%s.img", imageFileName));

        return imageFile;
    }

    private void loadPhoto(){
        if (imageFile != null){
            Glide.with(this).load(imageFile).into(photo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            loadPhoto();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        sensorLight.setText(String.format("%s lux", lux));
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
