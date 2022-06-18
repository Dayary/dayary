package com.dayary.dayary;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.google.android.gms.tasks.Continuation;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class question_corDel extends AppCompatActivity {
    private ImageView imageView;
    private ImageView upload;
    private ImageView delete;
    private ImageView modify;
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
    private PostModel postModel;
    private String todayDate;
    private TextView queText;

    private View btn_home;
    private View btn_pen;
    private View btn_loc;
    private View btn_cal;
    private View btn_list;
    private View btn_drawing;
    private View btn_eras;

    private int saveflag;
    Bitmap image;
    Bitmap image2;

    private final int GET_GALLERY_IMAGE = 200;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writequestion_cor_del);

        saveflag = 0;

        Intent intent = getIntent();
        postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        imageView = findViewById(R.id.rectangle_1);
        editText = findViewById(R.id.today_i_am_);
        editLength = findViewById(R.id.some_id);

        queText = findViewById(R.id.q_what_s_yo);
        todayDate = getTodayDate();
        int day = Integer.parseInt(todayDate.substring(8, 10));
        Resources res = getResources();
        String[] planets = res.getStringArray(R.array.queList);
        queText.setText(planets[day%16]);

        //로컬 디바이스의 날짜를 가져옴
        CurDate = getTodayDate();

        btn_drawing = findViewById(R.id.icons8_pen_);
        btn_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                startActivityForResult(intent, 200);
            }
        });
        btn_eras = findViewById(R.id.icons8_eras);
        btn_eras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(0);
            }
        });


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
                        String editTextData = returnValue.substring(idx3 + 5 + 6, idx4);
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
                dialog = new ProgressDialog(question_corDel.this);
                dialog.setMessage("Loading");
                dialog.show();
                //기존 사진/정보 삭제
                database.child("user").child(postModel.userId).child(lastDate).removeValue();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                if (flag == 0) {

                    final String uid = postModel.getUserId();

                    postModel.text = "[ques"+day%16+"]" + editText.getText().toString();
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
                    Toast.makeText(question_corDel.this, "업로드 성공", Toast.LENGTH_LONG).show();

                    flag = 0;

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("model", (Serializable) postModel);
                    startActivity(intent);
                    finish();

                } else if (flag == 1) {
                    if(saveflag == 1){
                        final String uid = postModel.getUserId();
                        FirebaseStorage mStorage = FirebaseStorage.getInstance();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        byte[] data = baos.toByteArray();
                        StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(finalCurDate).child("drawImage");
                        UploadTask uploadTask = storageReference.putBytes(data);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                    throw task.getException();
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    System.out.println(downloadUri);
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    postModel.text = "[ques"+day%16+"]" + editText.getText().toString();
                                    postModel.photoName = "drawing Image";
                                    postModel.photo = String.valueOf(downloadUri);
                                    postModel.photoLatitude = "999999.999999";
                                    postModel.photoLongitude = "999999.999999";
                                    database.child("user").child(postModel.getUserId()).child(String.valueOf(finalCurDate1)).push().setValue(postModel);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 3000);
                                    Toast.makeText(question_corDel.this, "DB Upload success", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("model", (Serializable) postModel);
                                    startActivity(intent);
                                    finish();
                                } else {

                                }
                            }
                        });
                    }else if (saveflag == 2) {
                        final String uid = postModel.getUserId();

                        FirebaseStorage mStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(lastDate).child(PhotoName);
                        storageReference.delete();
                        final File file = new File(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                        //이미지 자동회전 방지
                        try {
                            ExifInterface exif = new ExifInterface(imagePath);
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            bitmap = rotateBitmap(bitmap, orientation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image2 = Bitmap.createScaledBitmap(bitmap, 300, 400, false);
                        image2.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        byte[] data = baos.toByteArray();
                        storageReference = mStorage.getReference().child("userImages").child(uid).child(finalCurDate).child(file.getName());
                        UploadTask uploadTask = storageReference.putBytes(data);

                        StorageReference finalStorageReference = storageReference;
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                    throw task.getException();
                                return finalStorageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    System.out.println(downloadUri);
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    postModel.text = "[ques"+day%16+"]" + editText.getText().toString();
                                    postModel.photoName = file.getName();
                                    postModel.photo = String.valueOf(downloadUri);
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
                                    Toast.makeText(question_corDel.this, "DB Upload success", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.putExtra("model", (Serializable) postModel);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });

                    }

                }
            }
        });

        delete = findViewById(R.id.rectangle_4);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(question_corDel.this);
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

                Toast.makeText(question_corDel.this, "일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("model", (Serializable) postModel);
                startActivity(intent);
                finish();
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
        //리스트 이동
        btn_list = findViewById(R.id.icons8_jour);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), listActivity.class);
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

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("user").child(postModel.userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            try {
                                String[] value = task.getResult().getValue().toString().split("\\}\\}, ");
                                value[0] = value[0].substring(1);
                                for (int i = 0; i < value.length; i++) {
                                    value[i] = value[i].substring(0, 10);
                                }
                                Intent intent = new Intent(getApplicationContext(), mapActivity.class);
                                intent.putExtra("model", (Serializable) postModel);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(question_corDel.this, "작성한 일기기 없습니다!\n일기를 작성해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

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
                            try {
                                String[] value = task.getResult().getValue().toString().split("\\}\\}, ");
                                value[0] = value[0].substring(1);
                                for (int i = 0; i < value.length; i++) {
                                    value[i] = value[i].substring(0, 10);
                                }
                                Intent intent = new Intent(getApplicationContext(), calendarActivity.class);
                                intent.putExtra("cal", value);
                                intent.putExtra("model", (Serializable) postModel);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(question_corDel.this, "작성한 일기기 없습니다!\n일기를 작성해주세요!", Toast.LENGTH_SHORT).show();
                            }
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
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imagePath = getRealPath(selectedImageUri);

            try {
                ExifInterface exif = new ExifInterface(imagePath);

                float[] latLong = new float[2];

                if (exif == null) {
                    latLong[0] = Float.parseFloat("999999.999999");
                    latLong[1] = Float.parseFloat("999999.999999");
                } else {
                    exif.getLatLong(latLong);

                    latitude = String.valueOf(latLong[0]);
                    longitude = String.valueOf(latLong[1]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(latitude);
            System.out.println(longitude);

            imageView.setImageURI(selectedImageUri);
            //flag == 1 사진/그림 수정 작업 진행
            flag = 1;
            //saveflag == 2 /사진
            saveflag = 2;

        }
        //Pop창 선택
        todayDate = getTodayDate();
        Intent intent = null;
        if (requestCode == 1) {
            if (resultCode == 0) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(question_corDel.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), normalWrite.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else if (resultCode == 1) {
                if (todayDate.equals(lastDate)) {
                    intent = new Intent(getApplicationContext(), question_corDel.class);
                    intent.putExtra("model", (Serializable) postModel);
                    Toast.makeText(question_corDel.this, "작성한 글이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(getApplicationContext(), writequestion.class);
                    intent.putExtra("model", (Serializable) postModel);
                }
                startActivity(intent);
            } else {

            }
        }
        if (requestCode == 200) {
            if (resultCode == 200) {
                if (data != null) {
                    image = (Bitmap) data.getExtras().get("image");
                    flag = 1;
                    saveflag = 1;
                    imageView.setImageBitmap(image);
                }
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
        super.onDestroy();
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}