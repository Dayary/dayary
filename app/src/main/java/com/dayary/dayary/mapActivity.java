package com.dayary.dayary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        sampleList = (ArrayList<GeoModel>) intent.getSerializableExtra("geo");

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.47698, 0.0000), 5));
        getMarkerItems();
    }

    public Marker addMarker(GeoModel geoModel) {
        LatLng position = new LatLng(geoModel.getLat(), geoModel.getLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        return mMap.addMarker(markerOptions);
    }

    private void getMarkerItems() {

        for(GeoModel geoModel : sampleList)
            System.out.println(geoModel);
        for(GeoModel geoModel : sampleList)
            addMarker(geoModel);
    }
}
