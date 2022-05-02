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
        editTextPassword = (EditText) findViewById(R.id.line_em);
        editTextCheckPassWord = (EditText)findViewById(R.id.confirm_pas);
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

        buttonBack.setOnClickListener(new View.OnClickListener(){

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
                    // 이메일과 비밀번호가 공백이 아닌 경우
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
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
                            // 회원가입 성공시
                            Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // 계정이 중복된 경우
                            Toast.makeText(SignUpActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}