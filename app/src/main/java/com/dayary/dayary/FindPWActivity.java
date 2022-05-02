package com.dayary.dayary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPWActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);

        ImageView pen = findViewById(R.id.findpw_penicon);
        pen.setVisibility(View.INVISIBLE);

        editTextEmail = (EditText) findViewById(R.id.findpw_email);
        TextView sendButton = findViewById(R.id.find_passwo);
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFoucs) {
                if (hasFoucs) {
                    pen.setVisibility(View.VISIBLE);
                } else {
                    pen.setVisibility(View.INVISIBLE);
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = editTextEmail.getText().toString().trim();
                firebaseAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(FindPWActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(FindPWActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                } else {
                                    Toast.makeText(FindPWActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        RelativeLayout backButton = findViewById(R.id.chevron_lef);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
