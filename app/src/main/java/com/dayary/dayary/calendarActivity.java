package com.dayary.dayary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class calendarActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private View btn_pen;
    private View btn_loc;

    private PostModel postModel;
    private Query query0;
    private Collection<CalendarDay> dayCollection;
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //달력 커스텀
        calendarView = findViewById(R.id.calendarView);
        calendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new oneDayDecorator()
                //new EventDecorator(Color.parseColor("#62A60C"),)
        );
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                Date inputText = day.getDate();
                String[] calendarHeaderElements = inputText.toString().split(" ");
                StringBuilder calendarHeaderBuilder = new StringBuilder();
                calendarHeaderBuilder.append(calendarHeaderElements[5])
                        .append(" ")
                        .append(calendarHeaderElements[1]);
                return calendarHeaderBuilder.toString();
            }
        });
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        calendarView.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance);
        calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);

        //기록 날짜 추출
        Intent intent = getIntent();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        postModel = (PostModel) intent.getSerializableExtra("model");
        mAuth = FirebaseAuth.getInstance();

        query0 = database.child("user").child(postModel.userId);
        query0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        if (dataSnapshot.hasChildren()) {
                            Date date = null;
                            String temp = dataSnapshot.getKey();
                            temp = temp.substring(0, 10);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                date = formatter.parse(temp.substring(0, 10));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            System.out.println(cal);
                            CalendarDay day = null;
                            day.copyTo(cal);
                            System.out.println(day);
                            dayCollection.add(day);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_pen = findViewById(R.id.icons8_penc);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupClick(view);
            }
        });

        //지도로 이동하는 버튼
        btn_loc = findViewById(R.id.icons8_loca);
        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mapActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
                finish();
            }
        });
    }

    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        //intent.putExtra("model", (Serializable) postModel);
        startActivityForResult(intent, 1);
    }
}

class SaturdayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public SaturdayDecorator() {
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SATURDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.BLUE));
    }
}

class SundayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public SundayDecorator() {
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}

class oneDayDecorator implements DayViewDecorator {
    private CalendarDay date = CalendarDay.today();

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        System.out.println(date);
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#62A60C")));
    }
}

class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, HashSet<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}



