package com.dayary.dayary;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class listActivity extends AppCompatActivity {

    private PostModel postModel;
    private Query query1;
    private String[] data;
    private String[][] covertData;
    private Query query2;
    private int flag = -1;

    private View btn_home;
    private View btn_pen;
    private View btn_loc;
    private View btn_cal;


    private GridView gridView;
    private String lastDate = "";
    private String todayDate;

    public interface ImageItemClickListener {
        void onImageItemClick(int a_imageResId);
    }

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
                try {
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
                } catch (Exception e) {
                    System.out.println();
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

                            try {
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
                            } catch (Exception e) {
                                Toast.makeText(listActivity.this, "작성한 일기기 없습니다!\n일기를 작성해주세요!", LENGTH_SHORT).show();
                            }


                        }
                    }
                });

            }
        });

    }

    private void bindGrid() {
        List<GridItem> itemList = new ArrayList<>();
        for (int i = 0; i < covertData.length; i++) {
            itemList.add(new GridItem(i, covertData[i][0], covertData[i][1], covertData[i][2], covertData[i][3]));
        }

        gridView = (GridView) findViewById(R.id.gridView);
        GridArrayAdapter gridAdapter = new GridArrayAdapter(this, itemList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                final GridItem item = (GridItem) gridAdapter.getItem(a_position);
                int year = Integer.parseInt(item.getYear());
                int month = Integer.parseInt(item.getMonth());
                int dayy = Integer.parseInt(item.getDay());

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
                                if (tempFlag.equals("[free]"))
                                    flag = 1;
                                else if (tempFlag.equals("[ques]"))
                                    flag = 0;

                                if (flag == 1) {
                                    flag = -1;
                                    Intent intent = new Intent(getApplicationContext(), showFreeActivity.class);
                                    intent.putExtra("model", (Serializable) postModel);
                                    intent.putExtra("query", finalQueryDate);
                                    startActivity(intent);
                                } else if (flag == 0) {
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
                    Toast.makeText(listActivity.this, "작성한 글이 있습니다!", LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(listActivity.this, "작성한 글이 있습니다!", LENGTH_SHORT).show();
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
