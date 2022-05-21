package com.dayary.dayary;

import static com.google.firebase.database.core.RepoManager.clear;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private PostModel postModel;
    ArrayList<GeoModel> sampleList = new ArrayList<>();
    private Query query1;
    private Query query3;
    private String[] data;
    private String todayDate;
    private String lastDate;
    Bitmap compressBitmap;

    private View btn_home;
    private View btn_pen;
    private View btn_cal;
    private View btn_list;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        getData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleView);
        mapFragment.getMapAsync(this);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query1 = database.child("user").child(postModel.userId).limitToLast(1);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        String returnValue = snapshot.getValue().toString();
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
        //리스트 이동
        btn_list = findViewById(R.id.icons8_jour);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), listActivity.class);
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
        //캘린더 이동
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


    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("model", (Serializable) postModel);
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
        todayDate = getTodayDate();
        Intent intent = null;
        if (requestCode == 1) {
            if (resultCode == 0) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(mapActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(mapActivity.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else {

            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getMarkerItems();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.47698, 0.0000), 10));
    }

    public Marker addMarker(GeoModel geoModel) {
        LatLng position = new LatLng(geoModel.getLat(), geoModel.getLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(geoModel.getBitmap()));
        return mMap.addMarker(markerOptions);
    }

    private void getMarkerItems() {

        for (GeoModel geoModel : sampleList)
            addMarker(geoModel);
    }

    private void getData() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query3 = database.child("user").child(postModel.userId);
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String returnValue = snapshot.getValue().toString().substring(1);
                System.out.println(returnValue);
                Log.d("return value", returnValue + "");
                data = returnValue.split("\\}\\}, ");
                data[data.length - 1] = data[data.length - 1].substring(0, data[data.length - 1].length() - 3);
                for (int i = 0; i < data.length; i++) {
                    int idx1 = data[i].indexOf("photoLongitude=");
                    int idx2 = data[i].indexOf(", text=");
                    double lng = Double.parseDouble(data[i].substring(idx1 + 15, idx2));

                    int idx3 = data[i].indexOf("photoLatitude=");
                    double lat = Double.parseDouble(data[i].substring(idx3 + 14, data[i].length() - 1));

                    int idx4 = data[i].indexOf("photo=");
                    int idx5 = data[i].indexOf(", photoLongitude");
                    String imgURL = data[i].substring(idx4 + 6, idx5);
                    Bitmap bitmap = getBitmap(imgURL);
                    Bitmap smallMaker = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
                    sampleList.add(new GeoModel(lat, lng, smallMaker));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Bitmap getBitmap(String imgPath) {
        final Bitmap[] bitmap = new Bitmap[1];
        Thread imgThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                    compressBitmap = compressBitmap(bitmap[0]);
                } catch (IOException e) {
                }
            }
        };
        imgThread.start();
        try {
            imgThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return compressBitmap;
        }
    }
    private Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return compressedBitmap;
    }
}
