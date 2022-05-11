package com.dayary.dayary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class calendarActivity extends AppCompatActivity {


    private View btn_pen;
    private View btn_loc;

    private PostModel postModel;
    private MaterialCalendarView calendarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        calendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new oneDayDecorator()
        );
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
class SaturdayDecorator implements DayViewDecorator{
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
class SundayDecorator implements DayViewDecorator{
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
class oneDayDecorator implements DayViewDecorator{
    private CalendarDay date = CalendarDay.today();

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#62A60C")));
    }
}



