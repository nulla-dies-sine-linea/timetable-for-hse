package org.hse.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ItemAdapter adapter;
    private TextView viewDate;
    protected MainViewModel mainViewModel;

    static public String ARG_ID = "0", ARG_TYPE = "1", ARG_MODE = "2", ARG_TIME = "3";
    static public String name, DEFAULT_NAME = "нет данных";
    static BaseActivity.ScheduleType type;
    static BaseActivity.ScheduleMode mode;
    static Date date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewDate = findViewById(R.id.time);

        type = (BaseActivity.ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (BaseActivity.ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        name = getIntent().getStringExtra(ARG_ID);
        if(name == null){
            name = DEFAULT_NAME;
        }

        if (date == null){
            date = new Date();
        }

        if (type == BaseActivity.ScheduleType.WEEK){
            date = (Date) getIntent().getSerializableExtra(ARG_TIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM", Locale.forLanguageTag("ru"));
            viewDate.setText(String.format("Сегодня %s", simpleDateFormat.format(date)));
        }
        else{
            viewDate.setText("");
        }

        TextView title = findViewById(R.id.title);

        recyclerView = (RecyclerView)findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 0));
        adapter = new ItemAdapter(new BaseActivity.OnItemClick() {
            public void onClick(ScheduleItem data) {
            }
        });
        recyclerView.setAdapter(adapter);

        initData();
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

        @Override
        public String[] getWeekdays() {
            return new String[] {"", "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
        }
    };

    private void initData() {
        mainViewModel.getTimeTableTeacherByDate(date).observe(this, new Observer<List<TimeTableWithTeacherEntity>>() {
            @Override
            public void onChanged(@org.jetbrains.annotations.Nullable List<TimeTableWithTeacherEntity> entitylist) {

                List<ScheduleItem> list = new ArrayList<>();

                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.forLanguageTag("ru"));
                SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd MMMM", myDateFormatSymbols);
                SimpleDateFormat sdf3 = new SimpleDateFormat("w", Locale.forLanguageTag("ru"));
                SimpleDateFormat sdf4 = new SimpleDateFormat("u", Locale.getDefault());


                for (TimeTableWithTeacherEntity entity: entitylist){

                    if (type == BaseActivity.ScheduleType.DAY && sdf2.format(entity.timeTableEntity.timeStart).compareTo(sdf2.format(date)) == 0){
                        ScheduleItem item = new ScheduleItem();
                        Log.d("tag", "1");
                        item.setStart(sdf1.format(entity.timeTableEntity.timeStart));
                        item.setEnd(sdf1.format(entity.timeTableEntity.timeEnd));
                        item.setType(entity.timeTableEntity.type == 0? "Лекция": "Практическое занятие");
                        item.setName(entity.timeTableEntity.subjName);
                        item.setPlace(String.format("%s, %s", entity.timeTableEntity.corp, entity.timeTableEntity.cabinet));
                        item.setTeacher(entity.teacherEntity.fio);
                        list.add(item);
                    }

                    if (type == BaseActivity.ScheduleType.WEEK &&
                            Integer.parseInt(sdf3.format(entity.timeTableEntity.timeStart)) == Integer.parseInt(sdf3.format(date)) &&
                            Integer.parseInt(sdf4.format(entity.timeTableEntity.timeStart)) >= Integer.parseInt(sdf4.format(date))){

                        ScheduleItemHeader header = new ScheduleItemHeader();
                        header.setTitle(sdf2.format(entity.timeTableEntity.timeStart));
                        list.add(header);

                        ScheduleItem item = new ScheduleItem();
                        item.setStart(sdf1.format(entity.timeTableEntity.timeStart));
                        item.setEnd(sdf1.format(entity.timeTableEntity.timeEnd));
                        item.setType(entity.timeTableEntity.type == 0? "Лекция": "Практическое занятие");
                        item.setName(entity.timeTableEntity.subjName);
                        item.setPlace(String.format("%s, %s", entity.timeTableEntity.corp, entity.timeTableEntity.cabinet));
                        item.setTeacher(entity.teacherEntity.fio);
                        list.add(item);
                    }
                }
                adapter.setDataList(list);
            }
        });

//        ScheduleItemHeader header = new ScheduleItemHeader();
//        header.setTitle(String.format("Понедельник, 28 января\r\n%s", name));
//        list.add(header);
//
//        ScheduleItem item = new ScheduleItem();
//        item.setStart("10:00");
//        item.setEnd("11:00");
//        item.setType("Практическое занятие");
//        item.setName("Анализ данных (анг)");
//        item.setPlace("Ауд. 503, Кочновский пр-д, д.3");
//        item.setTeacher("Пред. Гущим Михаил Иванович");
//        list.add(item);
//
//        item = new ScheduleItem();
//        item.setStart("12:00");
//        item.setEnd("13:00");
//        item.setType("Практическое занятие");
//        item.setName("Анализ данных (анг)");
//        item.setPlace("Ауд. 503, Кочновский пр-д, д.3");
//        item.setTeacher("Пред. Гущим Михаил Иванович");
//        list.add(item);
//        adapter.setDataList(list);
    }

    public void filterItem(){
        if (type == BaseActivity.ScheduleType.DAY){

        }
    }

    public final static class ItemAdapter extends
            RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_ITEM = 0;
        private final static int TYPE_HEADER = 1;

        private List<ScheduleItem> dataList = new ArrayList<>();
        private BaseActivity.OnItemClick onItemClick;

        public ItemAdapter(BaseActivity.OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
                return new ViewHolder(contactView, context, onItemClick);
            } else if (viewType == TYPE_HEADER) {
                View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
                return new ViewHolderHeader(contactView, context, onItemClick);
            }
            throw new IllegalArgumentException("Invalid view type");
        }

        public int getItemViewType(int position) {
            ScheduleItem data = dataList.get(position);
            if (data instanceof ScheduleItemHeader) {
                return TYPE_HEADER;
            }
            return TYPE_ITEM;
        }

        public void setDataList(List<ScheduleItem> list) {
            this.dataList = new ArrayList<>();
            if (dataList != null) {
                this.dataList.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ScheduleItem data = dataList.get(position);
            if (viewHolder instanceof ViewHolder) {
                ((ViewHolder) viewHolder).bind((ScheduleItem) data);
            } else if (viewHolder instanceof ViewHolderHeader) {
                ((ViewHolderHeader) viewHolder).bind((ScheduleItemHeader) data);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        private Context context;
        private BaseActivity.OnItemClick onItemClick;
        private TextView start;
        private TextView end;
        private TextView type;
        private TextView name;
        private TextView place;
        private TextView teacher;

        public ViewHolder (View itemView, Context context, BaseActivity.OnItemClick onItemClick){
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            teacher = itemView.findViewById(R.id.teacher);
        }

        public void bind(final ScheduleItem data){
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder{
        private Context context;
        private BaseActivity.OnItemClick onItemClick;
        private TextView title;

        public ViewHolderHeader(View itemView, Context context, BaseActivity.OnItemClick onItemClick){
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            title = itemView.findViewById(R.id.title);
        }

        public void bind(final ScheduleItemHeader data) {
            title.setText(data.getTitle());}
    }
}





