package com.dayary.dayary;

import androidx.activity.ComponentActivity;
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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class corDel extends AppCompatActivity {
    private ImageView imageView;
    private ImageView upload;
    private ImageView delete;
    private ImageView modify;
    private View home_view;
    private FirebaseAuth mAuth;
    private EditText editText;
    private String CurDate = "";
    private String lastDate = "";
    private Uri selectedImageUri;
    private String PhotoName;
    private String imagePath;
    private String latitude;
    private String longitude;
    private String imgURL;
    private Query query2;
    private TextView editLength;
    private int flag = 0;
    private ProgressDialog dialog;

    private final int GET_GALLERY_IMAGE = 200;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writequestion_cor_del);

        dialog = new ProgressDialog(corDel.this);
        dialog.setMessage("Loading");
        dialog.show();
        dialog.dismiss();

        Intent intent = getIntent();
        PostModel postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        imageView = findViewById(R.id.rectangle_1);
        editText = findViewById(R.id.today_i_am_);
        editLength = findViewById(R.id.some_id);

        //로컬 디바이스의 날짜를 가져옴
        CurDate = getTodayDate();

        upload = findViewById(R.id.rectangle_2);
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

            }
        });

        query2 = database.child("user").child(postModel.userId).child(lastDate).limitToLast(1);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        String returnValue = snapshot.getValue().toString();
                        int idx0 = returnValue.indexOf("=");
                        lastDate = returnValue.substring(1, idx0);
                        int idx1 = returnValue.indexOf("photo=");
                        int idx2 = returnValue.indexOf(", photoLongitude");
                        imgURL = returnValue.substring(idx1 + 6, idx2);
                        Glide.with(getApplicationContext()).load(imgURL).fitCenter().into(imageView);
                        int idx3 = returnValue.indexOf("text=");
                        int idx4 = returnValue.indexOf(", photoName=");
                        String editTextData = returnValue.substring(idx3 + 5, idx4);
                        editText.setText(editTextData);
                        int idx5 = returnValue.indexOf("photoName=");
                        int idx6 = returnValue.indexOf(", userId=");
                        PhotoName = returnValue.substring(idx5 + 10, idx6);
                        int idx7 = returnValue.indexOf("photoLatitude=");
                        int idx8 = returnValue.indexOf("}}}");
                        latitude = returnValue.substring(idx7 + 14, idx8);
                        int idx9 = returnValue.indexOf("photoLongitude=");
                        int idx10 = returnValue.indexOf(", text");
                        longitude = returnValue.substring(idx9 + 15, idx10);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        String finalCurDate = CurDate;
        String finalCurDate1 = CurDate;

        modify = findViewById(R.id.rectangle_3);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(corDel.this);
                dialog.setMessage("Updating");
                dialog.show();
                //기존 사진/정보 삭제
                database.child("user").child(postModel.userId).child(lastDate).removeValue();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                if (flag == 0) {

                    final String uid = postModel.getUserId();

                    postModel.text = editText.getText().toString();
                    postModel.photoName = PhotoName;
                    postModel.photo = imgURL;
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
                    Toast.makeText(corDel.this, "DB Update success", Toast.LENGTH_LONG).show();

                    flag = 0;
                    finish();

                } else if (flag == 1) {

                    //새로운 이미지/정보 업로드
                    FirebaseStorage mStorage = FirebaseStorage.getInstance();
                    final String uid = postModel.getUserId();

                    StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(lastDate).child(PhotoName);
                    storageReference.delete();
                    final Uri file = Uri.fromFile(new File(imagePath));
                    Log.d("Photo", "photo file : " + file);
                    storageReference = mStorage.getReference().child("userImages").child(uid).child(finalCurDate).child(file.getLastPathSegment());
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
                            System.out.println(postModel.text);
                            System.out.println(postModel.photoName);
                            System.out.println(postModel.photo);
                            System.out.println(postModel.photoLatitude);
                            System.out.println(postModel.photoLongitude);
                            //database.child("user").child(postModel.getUserId()).child(String.valueOf(finalCurDate1)).push().setValue(postModel);
                            String key = database.child("user").child(postModel.getUserId()).child(String.valueOf(finalCurDate1)).push().getKey();
                            Map<String, Object> postValue = postModel.toMap();
                            Map<String, Object> childUpdate = new HashMap<>();
                            childUpdate.put("/user" + "/" + postModel.userId + "/" + finalCurDate1 + "/" + key, postValue);
                            database.updateChildren(childUpdate);
                            flag = 0;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 3000);

                            Toast.makeText(corDel.this, "DB Update success", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        });

        delete = findViewById(R.id.rectangle_4);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(corDel.this);
                dialog.setMessage("Deleting");
                dialog.show();

                database.child("user").child(postModel.userId).child(lastDate).removeValue();
                final String uid = postModel.getUserId();
                FirebaseStorage mStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(lastDate).child(PhotoName);
                storageReference.delete();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);

                Toast.makeText(corDel.this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
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

    //갤러리에서 이미지를 가져오는 onActivityResult
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
            //flag == 1 사진 수정 작업 진행
            flag = 1;
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