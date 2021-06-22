package org.hse.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_students;
    private Button button_staff;
    private Button button_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        button_students = (Button)findViewById(R.id.button_students);
        button_staff = (Button)findViewById(R.id.button_staff);
        button_settings = (Button)findViewById(R.id.button_settings);

        button_students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showStudents();}
        });

        button_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showStaff();}
        });

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showSettings();}
        });
    }

    private void showStudents() {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    private void showStaff() {
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}