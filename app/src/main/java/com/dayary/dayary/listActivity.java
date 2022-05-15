package com.dayary.dayary;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class listActivity extends AppCompatActivity {

    private PostModel postModel;
    private Query query1;
    private String[] data;
    private String[][] covertData;

    private View btn_home;
    private View btn_pen;
    private View btn_loc;
    private View btn_cal;

    private String lastDate = "";
    private String todayDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query1 = database.child("user").child(postModel.userId).orderByKey();
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String returnValue = snapshot.getValue().toString().substring(1);
                data = returnValue.split("\\}\\}, ");
                covertData = new String[data.length][4];
                data[data.length - 1] = data[data.length - 1].substring(0, data[data.length - 1].length() - 3);
                Arrays.sort(data, Collections.reverseOrder());
                for (int i = 0; i < data.length; i++) {
                    covertData[i][0] = data[i].substring(0, 4);
                    covertData[i][1] = data[i].substring(5, 7);
                    covertData[i][2] = data[i].substring(8, 10);
                    int idx1 = data[i].indexOf("photo=");
                    int idx2 = data[i].indexOf(", photoLongitude=");
                    covertData[i][3] = data[i].substring(idx1 + 6, idx2);
                }
                bindGrid();
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
        btn_cal = findViewById(R.id.icons8_cale);
        btn_cal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("user").child(postModel.userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            String[] value = task.getResult().getValue().toString().split("\\}\\}, ");
                            value[0] = value[0].substring(1);
                            for (int i = 0; i < value.length; i++) {
                                value[i] = value[i].substring(0, 10);
                            }
                            Intent intent = new Intent(getApplicationContext(), calendarActivity.class);
                            intent.putExtra("cal", value);
                            intent.putExtra("model", (Serializable) postModel);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });

    }

    private void bindGrid() {
        List<GridItem> itemList = new ArrayList<>();
        for (int i = 0; i < covertData.length; i++) {
            itemList.add(new GridItem(covertData[i][0], covertData[i][1], covertData[i][2], covertData[i][3]));
        }
        GridView gridView = (GridView) findViewById(R.id.gridView);
        GridArrayAdapter gridAdapter = new GridArrayAdapter(this, itemList);
        gridView.setAdapter(gridAdapter);
    }
    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        startActivityForResult(intent, 1);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        todayDate = getTodayDate();
        Intent intent = null;
        if (requestCode == 1) {
            if (resultCode == 0) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(listActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(listActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else {

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTodayDate() {
        //로컬 디바이스의 날짜를 가져옴
        LocalDate todaysDate = LocalDate.now();
        int curday = todaysDate.getDayOfWeek().getValue();
        String day = "";
        switch (curday) {
            case 1:
                day = todaysDate + "-" + "Mon";
                break;
            case 2:
                day = todaysDate + "-" + "Tue";
                break;
            case 3:
                day = todaysDate + "-" + "Wed";
                break;
            case 4:
                day = todaysDate + "-" + "Thur";
                break;
            case 5:
                day = todaysDate + "-" + "Fri";
                break;
            case 6:
                day = todaysDate + "-" + "Sat";
                break;
            case 7:
                day = todaysDate + "-" + "Sun";
                break;
        }

        return day;
    }
}
