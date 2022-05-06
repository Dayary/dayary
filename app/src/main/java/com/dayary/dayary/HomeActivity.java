package com.dayary.dayary;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btn_pen;
    private TextView countView;
    private TextView dateView;
    private View contentView;
    private String currentDate;


    Retrofit retrofit;
    WeatherApi weatherApi;
    ImageView weatherIconView;
    private final static String appKey = "778f6bedba4efd3041cfb178bee32f77";

=======
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        PostModel postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();


        weatherIconView = (ImageView)findViewById(R.id.weather_icon);
        getWeather();
=======
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("user").child(postModel.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot != null) {
                        count++;
                    }
                }
                String countValue = Integer.toString(count);
                countView = findViewById(R.id.num_memories);
                countView.setText(countValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dateView = findViewById(R.id.date);
        Query query = database.child("user").child(postModel.userId).limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot != null) {
                        String returnValue = snapshot.getValue().toString();
                        int idx = returnValue.indexOf("=");
                        currentDate = returnValue.substring(1,idx);
                        dateView.setText(currentDate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        contentView = findViewById(R.id.image_home_ex);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("userImage/"+ postModel.userId+"/"+currentDate+"/").




        btn_pen = (Button) findViewById(R.id.icons8_penc);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
                ad.setPositiveButton("Free subject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), normalWrite.class);
                        intent.putExtra("model", (Serializable) postModel);
                        startActivity(intent);
                    }
                });

                ad.setNegativeButton("Today's question", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), writequestion.class);
                        intent.putExtra("model", (Serializable) postModel);
                        startActivity(intent);
                    }
                });
                ad.show();
            }
        });

    }

    public void getWeather(){
        System.out.println("test");
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create()).build();


        weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherModel> weatherCall = weatherApi.getWeather("Seoul", appKey);
        weatherCall.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if(!(response.isSuccessful())){
                    Toast.makeText(HomeActivity.this, response.code(), Toast.LENGTH_LONG).show();
                }
                WeatherModel mydata = response.body();
                Weather weather= mydata.getWeather().get(0);
                String todayWeather = weather.getMain();

                switch(todayWeather){
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

=======

}