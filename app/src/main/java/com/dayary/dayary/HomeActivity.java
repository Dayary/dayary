package com.dayary.dayary;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView countView;
    private TextView dateView;
    private ImageView contentView;
    private String lastDate;
    private String imgURL;

    private Query query0;
    private Query query1;
    private Query query2;

    private String todayDate;
    int count = 0;
    private ProgressDialog dialog;
    private PostModel postModel;
    private String[] arr = null;

    private View btn_pen;
    private View btn_loc;
    private View btn_cal;

    Retrofit retrofit;
    WeatherApi weatherApi;
    ImageView weatherIconView;
    private final static String appKey = "778f6bedba4efd3041cfb178bee32f77";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();

        weatherIconView = (ImageView) findViewById(R.id.weather_icon);
        getWeather();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(HomeActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 500);

        query0 = database.child("user").child(postModel.userId);
        query0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        if (dataSnapshot.hasChildren()) {
                            count++;
                        }
                    }
                }
                String countValue = Integer.toString(count);
                countView = findViewById(R.id.num_memories);
                countView.setText(countValue);
                count = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        todayDate = getTodayDate();
        dateView = findViewById(R.id.date);
        dateView.setText(todayDate);

        query1 = database.child("user").child(postModel.userId).limitToLast(1);
        query1.addValueEventListener(new ValueEventListener() {
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

        contentView = findViewById(R.id.image_home_ex);
        query2 = database.child("user").child(postModel.userId).limitToLast(1);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        String returnValue = snapshot.getValue().toString();
                        int idx1 = returnValue.indexOf("photo=");
                        int idx2 = returnValue.indexOf(", photoLongitude");
                        imgURL = returnValue.substring(idx1 + 6, idx2);
                        Glide.with(getApplicationContext()).load(imgURL).override(352, 470).fitCenter().into(contentView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //하단 버튼 이동
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
        // 캘린더 이동
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
                    System.out.println("1" + lastDate);
                    System.out.println("2" + todayDate);
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(HomeActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(HomeActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else {

            }
        }
    }

    //날씨 조회
    public void getWeather() {
        System.out.println("test");
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()).build();


        weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherModel> weatherCall = weatherApi.getWeather("Seoul", appKey);
        weatherCall.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (!(response.isSuccessful())) {
                    Toast.makeText(HomeActivity.this, response.code(), Toast.LENGTH_LONG).show();
                }
                WeatherModel mydata = response.body();
                Weather weather = mydata.getWeather().get(0);
                String todayWeather = weather.getMain();

                switch (todayWeather) {
                    case "Clear":
                        weatherIconView.setImageResource(R.drawable.clear_icon);
                        break;
                    case "Clouds":
                        weatherIconView.setImageResource(R.drawable.clouds_icon);
                        break;
                    case "Drizzle":
                    case "Rain":
                        weatherIconView.setImageResource(R.drawable.rain_icon);
                        break;
                    case "Snow":
                        weatherIconView.setImageResource(R.drawable.snow_icon);
                        break;
                    case "Thunderstorm":
                        weatherIconView.setImageResource(R.drawable.thunderstorm_icon);
                        break;
                    default:
                        weatherIconView.setImageResource(R.drawable.mist_icon);
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
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

    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }


}