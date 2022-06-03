package com.example.classroom.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.classroom.R;
import com.example.classroom.databinding.ActivityAddUserBinding;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddUserActivity extends AppCompatActivity {
    private ActivityAddUserBinding activityAddUserBinding;
    private static final int WRITE_CODE = 1;
    private Uri imageUri = null;
    private long dob = 0;
    private String gender = "male";
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddUserBinding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(activityAddUserBinding.getRoot());
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        activityAddUserBinding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddUserActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_CODE);
                }else {
                    pickImage();
                }
            }
        });
        activityAddUserBinding.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             Calendar calendar =Calendar.getInstance();
             int day = calendar.get(Calendar.DAY_OF_MONTH);
             int month = calendar.get(Calendar.MONTH);
             int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker=new DatePickerDialog(AddUserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        calendar.set(year, month ,dayOfMonth);
                        String myFormat = "EEE, MMM d, yy";
                        SimpleDateFormat simpleDate = new SimpleDateFormat(myFormat, Locale.US);
                        activityAddUserBinding.tvDate.setText(simpleDate.format(calendar.getTime()));
                        dob = calendar.getTimeInMillis();
                    }
                },year,month,day);
                picker.show();

            }
        });
        activityAddUserBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddUserActivity.this);
                builder.setTitle("Confirm ");
                builder.setMessage("Are u Sure to store data?");
                builder.setIcon(R.drawable.ic_baseline_warning_24);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        saveData();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //builder.show();
            }
        });

        activityAddUserBinding.groupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==0){
                    gender = "male";
                }else {
                    gender = "female";
                }
            }
        });
    }

    private void saveData() {
        if(activityAddUserBinding.edtName.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(activityAddUserBinding.edtCollage.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dob==0){
            Toast.makeText(this, "Please enter DOB", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(Constant.KEY_USER_ID, auth.getUid());
        userMap.put(Constant.KEY_USER_NAME, activityAddUserBinding.edtName.getText().toString());
        userMap.put(Constant.KEY_USER_COLLAGE, activityAddUserBinding.edtCollage.getText().toString());
        userMap.put(Constant.KEY_USER_DATA_OF_BIRTH, dob);
        userMap.put(Constant.KEY_USER_EMAIL, auth.getCurrentUser().getEmail());

        //userMap.put(Constant.KEY_USER_EMAIL, "");
        userMap.put(Constant.KEY_USER_GENDER, gender);
        userMap.put(Constant.KEY_USER_ADMIN, false);

        if (imageUri != null) {

            ProgressDialog progressDialog = new ProgressDialog(AddUserActivity.this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Spinner
            progressDialog.setMessage("Please waite!!");
            progressDialog.show();

            long time = Calendar.getInstance().getTimeInMillis();
            StorageReference reference = firebaseStorage.getReference("userImage/"+ time + ".png");
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                   سطر عشان اجيب الurl تبع الصورة اللي رفعتها
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userMap.put(Constant.KEY_USER_IMAGE, uri.toString());
                            firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS).document(auth.getUid())
                                    .set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(getBaseContext(), HomeActivity.class));
                                    finish();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserActivity.this, "TRY AGAIN!!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG: mylog:", e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setProgress((int)progress);
                }
            });
        }else {
            userMap.put(Constant.KEY_USER_IMAGE, "");
            firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                    .document(auth.getUid())
                    .set(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(getBaseContext(), HomeActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_CODE:
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void pickImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        getLauncher.launch(pickIntent);
    }

    ActivityResultLauncher<Intent> getLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    imageUri = intent.getData(); // URI
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_user_avatar).into(activityAddUserBinding.imgUser);

                }
            });
}