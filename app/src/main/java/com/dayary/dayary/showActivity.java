package com.dayary.dayary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class showActivity extends AppCompatActivity {

    private PostModel postModel;
    private Query query;
    private String imgURL;
    private ImageView imageView;
    private TextView textView;
    private TextView textLength;


    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        String queryData = intent.getStringExtra("query");
        System.out.println(queryData);
        imageView = findViewById(R.id.rectangle_1);
        textView = findViewById(R.id.today_i_am_);
        textLength = findViewById(R.id.some_id);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        query = database.child("user").child(postModel.userId).child(queryData);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.getKey().equals(queryData)) {
                        dialog = new ProgressDialog(showActivity.this);
                        dialog.setMessage("Loading");
                        dialog.show();
                        if (dataSnapshot != null) {
                            String returnValue = snapshot.getValue().toString();
                            System.out.println(returnValue);
                            int idx1 = returnValue.indexOf("photo=");
                            int idx2 = returnValue.indexOf(", photoLongitude");
                            imgURL = returnValue.substring(idx1 + 6, idx2);
                            Glide.with(getApplicationContext()).load(imgURL).fitCenter().into(imageView);
                            int idx3 = returnValue.indexOf("text=");
                            int idx4 = returnValue.indexOf(", photoName=");
                            String textData = returnValue.substring(idx3 + 5, idx4);
                            textView.setText(textData);
                            textLength.setText(textView.length()+"/200");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 500);
                        }
                    }
                    else{
                        System.out.println("here");
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onBackPressed() {
        this.finish();
    }
}
