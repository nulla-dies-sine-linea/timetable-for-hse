package org.hse.android;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "group", indices = {@Index(value = {"name"}, unique = true)})
public class GroupEntity{
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    @NotNull
    public String name = "";
}
