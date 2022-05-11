package com.dayary.dayary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private View home_view;
    private View btn_pen;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private PostModel postModel;
    ArrayList<GeoModel> sampleList = new ArrayList<>();
    private Query query3;
    private String[] data;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        getData();

        //sampleList = (ArrayList<GeoModel>) intent.getSerializableExtra("geo");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        home_view = findViewById(R.id.icons8_home);
        home_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
                finish();
            }
        });

        //글쓰기 이동하는 버튼
        btn_pen = findViewById(R.id.icons8_penc);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupClick(view);
            }
        });
    }

    public void mOnPopupClick(View v) {
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("model", (Serializable) postModel);
        startActivityForResult(intent, 1);
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
        dialog = new ProgressDialog(mapActivity.this);
        dialog.setMessage("Map Loading");
        dialog.show();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query3 = database.child("user").child(postModel.userId);
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String returnValue = snapshot.getValue().toString().substring(1);
                Log.d("return value", returnValue + "");
                data = returnValue.split("\\}\\}, ");
                System.out.println(data.length);
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
                    Bitmap smallMaker = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                    sampleList.add(new GeoModel(lat, lng, smallMaker));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000);
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
            return bitmap[0];
        }
    }
    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}
