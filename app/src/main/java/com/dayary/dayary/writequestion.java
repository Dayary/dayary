package com.dayary.dayary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;

public class writequestion extends AppCompatActivity {
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
    private ProgressDialog dialog;
    private String todayDate;
    private String lastDate = "";

    private View btn_home;
    private View btn_pen;
    private View btn_loc;
    private View btn_cal;
    private PostModel postModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writequestion);

        Intent intent = getIntent();
         postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        imageView = findViewById(R.id.rectangle_1);
        editText = findViewById(R.id.today_i_am_);
        editLength = findViewById(R.id.some_id);

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
        String finalCurDate = getTodayDate();
        String finalCurDate1 = getTodayDate();
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(writequestion.this);
                dialog.setMessage("Saving");
                dialog.show();

                final String uid = postModel.getUserId();
                FirebaseStorage mStorage = FirebaseStorage.getInstance();
                final Uri file = Uri.fromFile(new File(imagePath));
                Log.d("Photo", "photo file : " + file);

                StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(finalCurDate).child(file.getLastPathSegment());
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

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 3000);

                        Toast.makeText(writequestion.this, "DB Upload success", Toast.LENGTH_LONG).show();
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
        //글쓰기 이동
        btn_pen = findViewById(R.id.icons8_penc);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPopupClick(view);
            }
        });
        //Map 이동
        btn_loc = findViewById(R.id.icons8_loca);
        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), mapActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
                finish();
            }
        });
        // 캘린더 이동
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
                            for(int i = 0; i < value.length;i++) {
                                value[i] = value[i].substring(0,10);
                            }
                            Intent intent = new Intent(getApplicationContext(), calendarActivity.class);
                            intent.putExtra("cal",value);
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
        startActivityForResult(intent, 1);
    }
    //갤러리에서 이미지를 가져오는 onActivityResult + 글쓰기 Pop창 선택 onActivityResult
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //사진 가져오는 작업
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
        //Pop창 선택
        todayDate = getTodayDate();
        Intent intent = null;
        if (requestCode == 1) {
            if (resultCode == 0) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(writequestion.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
                finish();
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(writequestion.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
                finish();
            } else {

            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTodayDate() {
        //로컬 디바이스의 날짜를 가져옴
        LocalDate todaysDate = LocalDate.now();
        int curday = todaysDate.getDayOfWeek().getValue();
        String day = "";
        switch (curday) {
            case 1:
                day = todaysDate + "-" + "Mon";
                break;
            case 2:
                day = todaysDate + "-" + "Tue";
                break;
            case 3:
                day = todaysDate + "-" + "Wed";
                break;
            case 4:
                day = todaysDate + "-" + "Thur";
                break;
            case 5:
                day = todaysDate + "-" + "Fri";
                break;
            case 6:
                day = todaysDate + "-" + "Sat";
                break;
            case 7:
                day = todaysDate + "-" + "Sun";
                break;
        }

        return day;
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

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}