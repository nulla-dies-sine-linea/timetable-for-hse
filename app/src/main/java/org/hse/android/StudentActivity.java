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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class StudentActivity extends BaseActivity {

    public Spinner spinner;
    public ArrayAdapter<StudentActivity.Group> adapter;
    private TextView status, subject, cabinet, corp, teacher;
    protected MainViewModel mainViewModel;
    private List<Group> groups;
    public Date getCurrentTime() {return currentTime; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

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

    private void initGroupList(List<Group> groups){
        mainViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> list) {
                for (GroupEntity listEntity: list){
                    groups.add(new Group(listEntity.id, listEntity.name));
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
                    if (getSelectedGroup() != null && getSelectedGroup().getId().equals(listEntity.timeTableEntity.groupId)){
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

//    private void initTime(){
//        Date currentTime = new Date();
//        Locale locale = new Locale("ru");
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, E", locale);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
//        time.setText(String.format("Сейчас: %s", sdf.format(currentTime)));
//    }

    private void showSchedule(ScheduleType type){
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof Group)){
            return;
        }
        showScheduleImpl(ScheduleMode.STUDENT, type, (Group) selectedItem, currentTime);
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, Group group, Date time){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getName());
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_TIME, time);
        startActivity(intent);
    }

    private void initData() { initDataFromTimeTable(null); }

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

    static class Group{
        private Integer id;
        private String name;

        public Group(Integer id, String name){
            this.id = id;
            this.name = name;
        }

        public Integer getId(){
            return id;
        }

        public void setId(Integer id){
            this.id = id;
        }

        @Override
        public String toString(){
            return name;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }
    }
}

