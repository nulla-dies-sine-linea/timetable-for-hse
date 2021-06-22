package org.hse.android;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "time_table", foreignKeys =
        {@ForeignKey(entity = GroupEntity.class, parentColumns = "id", childColumns = "group_id", onDelete = CASCADE),
                @ForeignKey(entity = TeacherEntity.class, parentColumns = "id", childColumns = "teacher_id", onDelete = CASCADE)})
public class TimeTableEntity {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "subj_name")
    @NotNull
    public String subjName = "";

    @ColumnInfo(name = "type")
    public int type = 0;

    @ColumnInfo(name = "time_start")
    public Date timeStart;

    @ColumnInfo(name = "time_end")
    public Date timeEnd;

    @ColumnInfo(name = "subj_group")
    @NotNull
    public String subjGroup = "";

    @ColumnInfo(name = "cabinet")
    @NotNull
    public String cabinet = "";

    @ColumnInfo(name = "corp")
    @NotNull
    public String corp = "";

    @ColumnInfo(name = "group_id", index = true)
    public int groupId;

    @ColumnInfo(name = "teacher_id", index = true)
    public int teacherId;
}
