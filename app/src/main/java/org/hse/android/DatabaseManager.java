package org.hse.android;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private DatabaseHelper db;
    private static DatabaseManager instance;

    public static DatabaseManager getInstance(Context context){
        if (instance == null){
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseManager (Context context){
        db = Room.databaseBuilder(context,
                DatabaseHelper.class, DatabaseHelper.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback(){
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db){
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable(){
                            @Override
                            public void run() { initData(context); }
                        });
                    }
                })
                .build();
    }

    public HseDao getHseDao() { return db.hseDao(); }

    private void initData(Context context){
        List<GroupEntity> groups = new ArrayList<>();
        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "ПИ-18-1";
        groups.add(group);
        group = new GroupEntity();
        group.id = 2;
        group.name = "ПИ-18-2";
        groups.add(group);
        DatabaseManager.getInstance(context).getHseDao().insertGroup(groups);

        List<TeacherEntity> teachers = new ArrayList<>();
        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Викентьева Ольга Леонидовна";
        teachers.add(teacher);
        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Соколов Евгений Андреевич";
        teachers.add(teacher);
        DatabaseManager.getInstance(context).getHseDao().insertTeacher(teachers);

        List<TimeTableEntity> timeTables = new ArrayList<>();
        TimeTableEntity timeTable = new TimeTableEntity();
        timeTable.id = 1;
        timeTable.cabinet = "102";
        timeTable.subjGroup = "ПИ";
        timeTable.subjName = "Машинное обучение";
        timeTable.corp = "5К";
        timeTable.type = 0;
        timeTable.timeStart = dateFromString("2021-06-22 10:00");
        timeTable.timeEnd = dateFromString("2021-06-22 11:30");
        timeTable.groupId = 1;
        timeTable.teacherId = 2;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 2;
        timeTable.cabinet = "216";
        timeTable.subjGroup = "ПИ";
        timeTable.subjName = "Обеспечение качества и тестирование";
        timeTable.corp = "5К";
        timeTable.type = 0;
        timeTable.timeStart = dateFromString("2021-06-22 13:00");
        timeTable.timeEnd = dateFromString("2021-06-22 15:00");
        timeTable.groupId = 2;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        timeTable = new TimeTableEntity();
        timeTable.id = 3;
        timeTable.cabinet = "216";
        timeTable.subjGroup = "ПИ";
        timeTable.subjName = "Обеспечение качества и тестирование";
        timeTable.corp = "5К";
        timeTable.type = 1;
        timeTable.timeStart = dateFromString("2021-06-23 13:00");
        timeTable.timeEnd = dateFromString("2021-06-23 15:00");
        timeTable.groupId = 2;
        timeTable.teacherId = 1;
        timeTables.add(timeTable);

        Log.d("tag", timeTables.toString());
        DatabaseManager.getInstance(context).getHseDao()
                .insertTimeTable(timeTables);
        Log.d("tag", DatabaseManager.getInstance(context).getHseDao()
                .getAllTimetable().toString());
    }

    private Date dateFromString(String val){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm",
                        Locale.getDefault());
        try{
            return simpleDateFormat.parse(val);
        } catch (ParseException e){
            Log.e("TAG", e.getMessage());
        }
        return null;
    }
}

