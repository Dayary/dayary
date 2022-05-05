package com.dayary.dayary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;

public class LoginComActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button upload;
    private Button save;
    private EditText editText;
    private FirebaseAuth mAuth;
    private String imagePath;
    private Uri selectedImageUri;
    private final int GET_GALLERY_IMAGE = 200;
    private TextView textView;
    private String latitude;
    private String longitude;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);
        imageView = findViewById(R.id.imageView1);
        editText = findViewById(R.id.edit1);
        save = findViewById(R.id.save);
        LocalDate todaysDate = LocalDate.now();
        Intent intent = getIntent();
        PostModel postModel = (PostModel) intent.getSerializableExtra("model");
        System.out.println(postModel.getUserId());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        textView = findViewById(R.id.textView);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = user.getUid();
                FirebaseStorage mStorage = FirebaseStorage.getInstance();
                final Uri file = Uri.fromFile(new File(imagePath));
                Log.d("Photo", "photo file : " + file);

                StorageReference storageReference = mStorage.getReference().child("userImages").child(uid).child(file.getLastPathSegment());
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
                        database.child("user").child(postModel.getUserId()).child(String.valueOf(todaysDate)).push().setValue(postModel);
                        Toast.makeText(LoginComActivity.this, "DB Upload success", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

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

    public String getRealPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = managedQuery(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        c.moveToFirst();
        String path = c.getString(index);
        return path;
    }
}