package com.dayary.dayary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class listActivity extends AppCompatActivity {

    private PostModel postModel;
    private Query query1;
    private String[] data;
    private String[][] covertData;

    private Bitmap bitmap;
    private TextView dataView;
    private TextView date1;
    private ImageView view1;
    private TextView date2;
    private ImageView view2;

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
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < 4; j++) {
                        System.out.print(covertData[i][j] + " ");
                    }
                    System.out.println();
                }

                dataView = findViewById(R.id.date);
                dataView.setText(covertData[0][0] + "/" + covertData[0][1]);
                date1 = findViewById(R.id.date1);
                date2 = findViewById(R.id.date2);
                view1 = findViewById(R.id.image_list1);
                view2 = findViewById(R.id.image_list2);

                date1.setText(covertData[0][0] + "/" + covertData[0][1] + "/" + covertData[0][2]);
                drawImage(covertData[0][3], view1);

                date2.setText(covertData[1][0] + "/" + covertData[1][1] + "/" + covertData[1][2]);
                drawImage(covertData[1][3], view2);

                //Glide.with(getApplicationContext()).load(covertData[0][2]).fitCenter().into((ImageView) view1);
                //Glide.with(getApplicationContext()).load(covertData[1][2]).fitCenter().into((ImageView) view2);

                for (int i = 2; i < data.length - 1; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (i % 2 == 0) {
                            date1.setText();
                        } else if (i % 2 == 1) {

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ScrollView sc = (ScrollView) findViewById(R.id.scrollView);


    }

    public void drawImage(String imgURL, ImageView view) {
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            view.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
