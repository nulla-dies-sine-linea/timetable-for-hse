package org.hse.android;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "teacher", indices = {@Index(value = {"fio"}, unique = true)})
public class TeacherEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "fio")
    @NotNull
    public String fio = "";
}
