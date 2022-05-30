package com.example.classroom.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.classroom.R;
import com.example.classroom.adapter.ViewPagerAdapter;
import com.example.classroom.databinding.ActivityHomeBinding;
import com.example.classroom.databinding.BottomSheetDialogBinding;
import com.example.classroom.model.User;
import com.example.classroom.utils.Constant;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding activityHomeBinding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setupTab();
        setupDate();
        firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                .document(auth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {

 //                addSnapshotListener بيعمل الrealTime عن طريقه
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
//                                تحويل الDocumentSnapshot الى اوبجكت وتخزينه في الuser
                            user = value.toObject(User.class);
                            String imageUrl = value.getString(Constant.KEY_USER_IMAGE);
                            String name = value.getString(Constant.KEY_USER_NAME);
                            if(imageUrl!=null && !imageUrl.isEmpty()){
                                Picasso.get().load(imageUrl).into(activityHomeBinding.imgUser);
                            }else {
                                Picasso.get().load(R.drawable.ic_user_avatar).into(activityHomeBinding.imgUser);
                            }
                            Toast.makeText(HomeActivity.this,name , Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        bottomSheetDialog();
        activityHomeBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog= new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.create_menu);
                dialog.show();
            }
        });
    }
    private void bottomSheetDialog() {
        activityHomeBinding.tvGreeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this,R.style.bottomSheetTheme);
                BottomSheetDialogBinding binding = BottomSheetDialogBinding.inflate(getLayoutInflater());
                bottomSheetDialog.setContentView(binding.getRoot());
                bottomSheetDialog.show();

                binding.imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                if(user.getImage()!=null && !user.getImage().isEmpty()){
                    Picasso.get().load(user.getImage()).into(binding.imgUser);
                }else {
                    Picasso.get().load(R.drawable.ic_user_avatar).into(binding.imgUser);
                }
                binding.tvName.setText(user.getName());
                if(user.getGender().equals("male")){
                    binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.man, 0);
                }else {
                    binding.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.girl, 0);
                }
                binding.tvCollage.setText(user.getCollage());

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(user.getDateOfBirth());

                String myFormat = "EEE, MMM d, yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
                binding.tvDOB.setText(simpleDateFormat.format(calendar.getTime()));

                binding.logOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auth.signOut();
                        startActivity(new Intent(getBaseContext(), SignUpActivity.class));
                        finish();
                    }
                });
            }
        });
    }
    private void setupDate() {
       firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS).document(auth.getUid())
               .addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
               String name = value.getString(Constant.KEY_USER_NAME);
               Date date = new Date();

               Calendar calendar = Calendar.getInstance();
               calendar.setTime(date);
               calendar.get(Calendar.AM_PM);

               if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                   activityHomeBinding.tvGreeting.setText("Good Morning "+name);
               }else {
                   activityHomeBinding.tvGreeting.setText("Good evening "+name);
               }

           }
       });

    }

    private void setupTab() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        activityHomeBinding.viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(activityHomeBinding.tabLayout, activityHomeBinding.viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position== 0){
                            tab.setText("Feed");
                        }else {
                            tab.setText("Assigment");
                        }
                    }
                }).attach();
    }
}