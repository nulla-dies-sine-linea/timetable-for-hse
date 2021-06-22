package org.hse.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.TimeZone;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseActivity extends AppCompatActivity {

    public interface OnItemClick{
        public void onClick(ScheduleItem data);
    }

    private final static String TAG = "BaseActivity";
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";

    protected TextView time;
    protected Date currentTime;
    protected final String[] INTERNET_PERMISSION = {Manifest.permission.INTERNET};

    private OkHttpClient client = new OkHttpClient();

    public void checkPermission(){
        int permissionCheck = ActivityCompat.checkSelfPermission(this, INTERNET_PERMISSION[0]);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            getTime();
        } else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, INTERNET_PERMISSION[0])){
                showExplanation(getString(R.string.should_permit_title_string), getString(R.string.should_permit_text_string), INTERNET_PERMISSION[0], 0);
            }
            else{
                requestPermissions(INTERNET_PERMISSION, 0);
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
        if (requestCode == 0){
            if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED){
                getTime();
            }
        }
    }

    protected void getTime(){
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showTime(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException{
                parseResponse(response);
            }
        });
    }

    protected void initTime(){
        getTime();
    }

    private void parseResponse(Response response){
        Gson gson = new Gson();
        ResponseBody body = response.body();
        try{
            if (body == null){
                return;
            }
            String string = body.string();
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = simpleDateFormat.parse(currentTimeVal);

            runOnUiThread(() -> showTime(dateTime));

        } catch(Exception e){
//            Log.e("tag", e.getMessage());
        }
    }

    protected void showTime(Date dateTime){
        if (dateTime == null){
            currentTime = new Date();
            Locale locale = new Locale("ru");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, E", locale);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            time.setText(String.format("Сейчас: %s", sdf.format(currentTime)));
            return;
        }
        else{
            currentTime = dateTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, E", Locale.forLanguageTag("ru"));
            time.setText(String.format("Сейчас: %s", simpleDateFormat.format(currentTime)));
        }
    }

    enum ScheduleType{
        DAY,
        WEEK
    }

    enum ScheduleMode{
        STUDENT,
        TEACHER
    }
}


