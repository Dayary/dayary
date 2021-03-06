package com.dayary.dayary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCheckPassWord;
    private TextView buttonJoin;
    private RelativeLayout buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.e_mail);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextCheckPassWord = (EditText) findViewById(R.id.confirm_pas);
        buttonJoin = (TextView) findViewById(R.id.register2);
        buttonBack = (RelativeLayout) findViewById(R.id.pageBack);

        ImageView pen1 = findViewById(R.id.login_register_penicon1);
        ImageView pen2 = findViewById(R.id.login_register_penicon2);
        ImageView pen3 = findViewById(R.id.login_register_penicon3);

        pen1.setVisibility(View.INVISIBLE);
        pen2.setVisibility(View.INVISIBLE);
        pen3.setVisibility(View.INVISIBLE);

        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pen1.setVisibility(View.VISIBLE);
                    pen2.setVisibility(View.INVISIBLE);
                    pen3.setVisibility(View.INVISIBLE);
                } else {
                    pen1.setVisibility(View.INVISIBLE);
                    pen2.setVisibility(View.INVISIBLE);
                    pen3.setVisibility(View.INVISIBLE);
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pen1.setVisibility(View.INVISIBLE);
                    pen2.setVisibility(View.VISIBLE);
                    pen3.setVisibility(View.INVISIBLE);
                } else {
                    pen1.setVisibility(View.INVISIBLE);
                    pen2.setVisibility(View.INVISIBLE);
                    pen3.setVisibility(View.INVISIBLE);
                }
            }
        });

        editTextCheckPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pen1.setVisibility(View.INVISIBLE);
                    pen2.setVisibility(View.INVISIBLE);
                    pen3.setVisibility(View.VISIBLE);
                } else {
                    pen1.setVisibility(View.INVISIBLE);
                    pen2.setVisibility(View.INVISIBLE);
                    pen3.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // ???????????? ??????????????? ????????? ?????? ??????
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    // ???????????? ??????????????? ????????? ??????
                    Toast.makeText(SignUpActivity.this, "????????? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ???????????? ?????????
                            Toast.makeText(SignUpActivity.this, "???????????? ??????", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // ????????? ????????? ??????
                            Toast.makeText(SignUpActivity.this, "?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}