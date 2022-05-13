package com.dayary.dayary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class calendarActivity extends AppCompatActivity {

    private View btn_home;
    private View btn_pen;
    private View btn_loc;


    private String todayDate;
    private String lastDate;
    private String imgURL;

    private Query query;
    private Query query2;
    private int flag = -1;
    private PostModel postModel;
    ProgressDialog dialog;

    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //기록 날짜 추출
        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        String[] result = intent.getStringArrayExtra("cal");
        for (int i = 0; i < result.length; i++)
            System.out.println(result[i]);

        //달력 커스텀
        calendarView = findViewById(R.id.calendarView);
        calendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new oneDayDecorator()
        );
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendarView.setHeaderTextAppearance(R.style.CustomHeaderTextAppearance);
        calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance);
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //날짜 추출
                int idx1 = date.toString().indexOf("{");
                int idx2 = date.toString().indexOf("}");
                String[] time = date.toString().substring(idx1 + 1, idx2).split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]) + 1;
                int dayy = Integer.parseInt(time[2]);
                LocalDate tempDate = LocalDate.of(year, month, dayy);
                DayOfWeek dayOfWeek = tempDate.getDayOfWeek();
                String queryDate = "";
                if (month < 10) {
                    if (dayy < 10)
                        queryDate = year + "-" + "0" + month + "-" + "0" + dayy + "-" + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                    else
                        queryDate = year + "-" + "0" + month + "-" + dayy + "-" + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                } else {
                    if (dayy < 10)
                        queryDate = year + "-" + month + "-" + "0" + dayy + "-" + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                    else
                        queryDate = year + "-" + month + "-" + dayy + "-" + dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
                }
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                query2 = database.child("user").child(postModel.userId).child(queryDate);
                String finalQueryDate = queryDate;
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String returnValue = snapshot.getValue().toString();
                            if (returnValue != null) {
                                int idx1 = returnValue.indexOf("text=");
                                int idx2 = returnValue.indexOf(", photoName=");
                                String tempFlag = returnValue.substring(idx1 + 5, idx1 + 5 + 6);
                                System.out.println(tempFlag);
                                if(tempFlag.equals("[free]"))
                                    flag = 1;
                                else if (tempFlag.equals("[ques]"))
                                    flag = 0;

                                if(flag == 1) {
                                    flag = -1;
                                    Intent intent = new Intent(getApplicationContext(), showFreeActivity.class);
                                    intent.putExtra("model", (Serializable) postModel);
                                    intent.putExtra("query", finalQueryDate);
                                    startActivity(intent);
                                }
                                else if (flag == 0) {
                                    flag = -1;
                                    Intent intent = new Intent(getApplicationContext(), showQuesActivity.class);
                                    intent.putExtra("model", (Serializable) postModel);
                                    intent.putExtra("query", finalQueryDate);
                                    startActivity(intent);
                                }

                            }
                        } catch (NullPointerException e) {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query = database.child("user").child(postModel.userId).limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        String returnValue = snapshot.getValue().toString();
                        System.out.println(returnValue);
                        int idx = returnValue.indexOf("=");
                        lastDate = returnValue.substring(1, idx);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //하단 버튼 이동
        //홈으로 이동
        btn_home = findViewById(R.id.icons8_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
                finish();
            }
        });
        //글쓰기 이동
        btn_pen = findViewById(R.id.icons8_penc);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupClick(view);
            }
        });
        //Map 이동
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


    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            for (int i = 0; i < Time_Result.length; i++) {
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            calendarView.addDecorator(new EventDecorator(Color.parseColor("#62A60C"), calendarDays));
        }
    }

    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        startActivityForResult(intent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTodayDate() {
        //로컬 디바이스의 날짜를 가져옴
        LocalDate todaysDate = LocalDate.now();
        int curday = todaysDate.getDayOfWeek().getValue();
        String CurDate = "";
        switch (curday) {
            case 1:
                CurDate = todaysDate + "-" + "Mon";
                break;
            case 2:
                CurDate = todaysDate + "-" + "Tue";
                break;
            case 3:
                CurDate = todaysDate + "-" + "Wed";
                break;
            case 4:
                CurDate = todaysDate + "-" + "Thur";
                break;
            case 5:
                CurDate = todaysDate + "-" + "Fri";
                break;
            case 6:
                CurDate = todaysDate + "-" + "Sat";
                break;
            case 7:
                CurDate = todaysDate + "-" + "Sun";
                break;
        }

        return CurDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        System.out.println(resultCode);
        todayDate = getTodayDate();
        Intent intent = null;
        if (requestCode == 1) {
            if (resultCode == 0) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(calendarActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(calendarActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else {

            }
        }
    }
}

class EventDecorator implements DayViewDecorator {

    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color)); // 날자밑에 점
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
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#62A60C")));
    }
}



