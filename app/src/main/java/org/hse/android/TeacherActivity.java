package org.hse.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TeacherActivity extends BaseActivity {

    private TextView status, subject, cabinet, corp, teacher;
    public Spinner spinner;
    public ArrayAdapter<StudentActivity.Group> adapter;
    protected MainViewModel mainViewModel;
    private List<StudentActivity.Group> groups;
    public Date getCurrentTime() {return currentTime; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setContentView(R.layout.activity_stuff);

        spinner = findViewById(R.id.groupList);

        groups = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initGroupList(groups);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position);
                showTime(currentTime);
                Log.d("TAG", "selectedItem" + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        cabinet = findViewById(R.id.cabinet);
        corp = findViewById(R.id.corp);
        teacher = findViewById(R.id.teacher);

        if (currentTime == null){
            currentTime = new Date();
            Locale locale = new Locale("ru");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, E", locale);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            time.setText(String.format("Сейчас: %s", sdf.format(currentTime)));
        }

        View scheduleDay = findViewById(R.id.button_day);
        scheduleDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY));
        View scheduleWeek = findViewById(R.id.button_week);
        scheduleWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK));

        checkPermission();
//        initTime();
        initData();
    }

    private void showSchedule(ScheduleType type){
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof StudentActivity.Group)){
            return;
        }
        showScheduleImpl(ScheduleMode.TEACHER, type, (StudentActivity.Group) selectedItem, currentTime);
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, StudentActivity.Group group, Date time){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getName());
        Log.d("tag", group.getName());
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_TIME, time);
        startActivity(intent);
    }

    private void initGroupList(List<StudentActivity.Group> groups){
        mainViewModel.getTeachers().observe(this, new Observer<List<TeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TeacherEntity> list) {
                for (TeacherEntity listEntity: list){
                    groups.add(new StudentActivity.Group(listEntity.id, listEntity.fio));
                }
                adapter.clear();
                adapter.addAll(groups);
            }
        });
    }

    @Override
    protected void showTime(Date dateTime){
        super.showTime(dateTime);
        mainViewModel.getTimeTableTeacherByDate(dateTime).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@Nullable List<TimeTableWithTeacherEntity> list) {
                for (TimeTableWithTeacherEntity listEntity : list){
                    Log.d("tag", listEntity.timeTableEntity.subjName + " " + listEntity.teacherEntity.fio);
                    if (getSelectedGroup() != null && getSelectedGroup().getId().equals(listEntity.timeTableEntity.teacherId)){
                        Log.d("tag", String.valueOf(getSelectedGroup().getId() - 1));
                        Log.d("tag", String.valueOf(listEntity.timeTableEntity.groupId));
                        initDataFromTimeTable(listEntity);
                    }
                }
            }
        });
    }

    private StudentActivity.Group getSelectedGroup(){
        Object selectedItem = spinner.getSelectedItem();
        return (StudentActivity.Group) selectedItem;
    }
    private void initDataFromTimeTable(TimeTableWithTeacherEntity timeTableWithTeacherEntity){
        if (timeTableWithTeacherEntity == null){
            status.setText("Нет пар");
            subject.setText("Дисциплина");
            cabinet.setText("Кабинет");
            corp.setText("Корпус");
            teacher.setText("Преподаватель");
            return;
        }

        status.setText("Идёт пара");
        TimeTableEntity timeTableEntity = timeTableWithTeacherEntity.timeTableEntity;

        subject.setText(timeTableEntity.subjName);
        cabinet.setText(timeTableEntity.cabinet);
        corp.setText(timeTableEntity.corp);
        teacher.setText(timeTableWithTeacherEntity.teacherEntity.fio);
    }

//    private void initTime(){
//        Date currentTime = new Date();
//        Locale locale = new Locale("ru");
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, E", locale);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
//        time.setText(String.format("Сейчас: %s", sdf.format(currentTime)));
//    }

    private void initData(){ initDataFromTimeTable(null); }
}

