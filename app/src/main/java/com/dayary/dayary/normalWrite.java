package com.dayary.dayary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class normalWrite extends AppCompatActivity {
    private View home_view;
    private ImageView upload;
    private ImageView save;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private String imagePath;
    private Uri selectedImageUri;
    private final int GET_GALLERY_IMAGE = 200;
    private String latitude;
    private String longitude;
    private EditText editText;
    private TextView editLength;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normalwrite);

        //Intent를 통해서 기존의 정보를 가져옴
        Intent intent = getIntent();
        PostModel postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        //findViewById
        imageView = findViewById(R.id.rectangle_1);
        editText = findViewById(R.id.today_i_am_);
        editLength = findViewById(R.id.some_id);

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
        System.out.println(CurDate);

        //upload 버튼 동작시 갤러리에서 이미지를 가져옴.
        upload = findViewById(R.id.rectangle_2);
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
        //save 버튼 동작시 파이어베이스 realtimeDB, Storage에 저장
        save = findViewById(R.id.rectangle_3);
        String finalCurDate = CurDate;
        String finalCurDate1 = CurDate;
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String uid = postModel.getUserId();
                FirebaseStorage mStorage = FirebaseStorage.getInstance();
                final Uri file = Uri.fromFile(new File(imagePath));
                Log.d("Photo", "photo file : " + file);

                StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(finalCurDate).child(file.getLastPathSegment());
                System.out.println(file.getPath());
                storageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while (!imageUrl.isComplete()) ;

                        System.out.println(user);
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        System.out.println(database);
                        postModel.text = editText.getText().toString();
                        postModel.photoName = file.getLastPathSegment();
                        postModel.photo = imageUrl.getResult().toString();
                        postModel.photoLatitude = latitude;
                        postModel.photoLongitude = longitude;
                        database.child("user").child(postModel.getUserId()).child(String.valueOf(finalCurDate1)).push().setValue(postModel);
                        Toast.makeText(normalWrite.this, "DB Upload success", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

        });

        //현재 글자수 표현
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editLength.setText(editable.length() + "/200");
            }
        });

        home_view = findViewById(R.id.icons8_home);
        //홈으로 이동하는 버튼
        home_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
            }
        });
    }

    //이미지를 가져오는 onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imagePath = getRealPath(selectedImageUri);

            System.out.println(selectedImageUri);
            System.out.println(imagePath);

            try {
                ExifInterface exif = new ExifInterface(imagePath);
                float[] latLong = new float[2];
                exif.getLatLong(latLong);
                latitude = String.valueOf(latLong[0]);
                longitude = String.valueOf(latLong[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageURI(selectedImageUri);
        }
    }

    //이미지 URL -> 이미지 절대경로를 가져옴
    public String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = managedQuery(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        c.moveToFirst();
        String path = c.getString(index);
        return path;
    }

}