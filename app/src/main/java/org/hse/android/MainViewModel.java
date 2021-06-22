package org.hse.android;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainViewModel extends AndroidViewModel {
    private HseRepository repository;

    public MainViewModel(@NotNull Application application){
        super(application);
        repository = new HseRepository(application);
    }

    public LiveData<List<GroupEntity>> getGroups() { return repository.getGroups(); }
    public LiveData<List<TeacherEntity>> getTeachers() { return repository.getTeachers(); }
    public LiveData<List<TimeTableEntity>> getTimeTables() { return repository.getTimeTables(); }
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherByDate(Date date){
        return repository.getTimeTableWithTeacherByDate(date);
    }

//    public LiveData<Date> getTime() {
//        getTimeFromURL();
//        return date;
//    }
//
//    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";
//    private OkHttpClient client = new OkHttpClient();
//    private LiveData<Date> date;
//
//    protected void getTimeFromURL(){
//        Request request = new Request.Builder().url(URL).build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Log.e("tag", e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response)
//                    throws IOException{
//                parseResponse(response);
//            }
//        });
//    }
//
//    private void parseResponse(Response response) {
//        Gson gson = new Gson();
//        ResponseBody body = response.body();
//        try {
//            if (body == null) {
//                return;
//            }
//            String string = body.string();
//            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
//            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date dateTime = simpleDateFormat.parse(currentTimeVal);
//
//            date = dateTime;
////            runOnUiThread(() -> showTime(dateTime));
//
//        } catch (Exception e) {
//            Log.e("tag", e.getMessage());
//        }
//    }
}
