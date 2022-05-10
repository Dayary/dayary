package com.dayary.dayary;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.time.LocalDate;

public class PopupActivity extends Activity {
    private View subject;
    private View question;
    private String todayDate;
    private Query query1;
    private String lastDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select);

        subject = findViewById(R.id.icons8_spea);
        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result",0);
                setResult(0,intent);
                finish();
            }
        });
        question = findViewById(R.id.icons8_ques);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result",1);
                setResult(1,intent);
                finish();
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            Intent intent = new Intent();
            setResult(-1,intent);
            finish();
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        Intent intent = new Intent();
        setResult(-1,intent);
        finish();
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

}
