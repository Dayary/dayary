package com.dayary.dayary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class listActivity extends AppCompatActivity {

    private PostModel postModel;
    private Query query1;
    private String[] data;
    private String[][] covertData;
    private ProgressDialog dialog;

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
                    System.out.println(data[i]);
                }
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

    }

    private void bindGrid() {
        List<GridItem> itemList = new ArrayList<>();
        for(int i =0; i<covertData.length;i++) {
            itemList.add(new GridItem(covertData[i][0],covertData[i][1],covertData[i][2],covertData[i][3]));
        }
        GridView gridView = (GridView) findViewById(R.id.gridView);
        GridArrayAdapter gridAdapter = new GridArrayAdapter(this, itemList);
        gridView.setAdapter(gridAdapter);
    }
    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}
