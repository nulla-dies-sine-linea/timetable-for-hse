package org.hse.android;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class HseRepository {
    private DatabaseManager databaseManager;
    private HseDao dao;

    public HseRepository(Context context){
        databaseManager = DatabaseManager.getInstance(context);
        dao = databaseManager.getHseDao();
    }

    public LiveData<List<GroupEntity>> getGroups() { return dao.getAllGroup(); }
    public LiveData<List<TeacherEntity>> getTeachers() { return dao.getAllTeacher(); }
    public LiveData<List<TimeTableEntity>> getTimeTables() { return dao.getAllTimetable(); }
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableWithTeacherByDate(Date date){
        return dao.getTimetableTeacher();
    }
    public LiveData<List<TimeTableWithTeacherEntity>> getTimeTableWithStudentByDateAndId(Date date, Integer id){
        return dao.getTimetableTeacher();
    }
}
