package com.example.classroom.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.classroom.databinding.DialogAddPostBinding;
import com.example.classroom.model.Post;
import com.example.classroom.model.User;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPostDialog extends DialogFragment {
    private DialogAddPostBinding dialogAddPostBinding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private User user;
    private List<String> listStudent = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserData();
        getAllUsersIds();
        dialogAddPostBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDialog().dismiss();
            }
        });
        dialogAddPostBinding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogAddPostBinding.edtPostMessage.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Plz add message", Toast.LENGTH_SHORT).show();
                    return;
                }

                String text = dialogAddPostBinding.edtPostMessage.getText().toString();
                Date date = new Date();


                String uid = auth.getUid();
                DocumentReference documentReference = firebaseFirestore.collection(Constant.KEY_COLLECTION_POST).document();
                String docId = documentReference.getId();

                Post post = new Post(docId, text, date, uid, user.getName(),listStudent);


                documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getDialog().dismiss();
                        Toast.makeText(getContext(), "POSTED", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "TRY AGAIN!!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.getMessage());
                    }
                });
            }
        });

    }
    private void getAllUsersIds() {
        firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task!=null && task.isSuccessful()){
                            for(DocumentSnapshot snapshot: task.getResult().getDocuments()){
                                //User u = snapshot.toObject(User.class);
                                listStudent.add(snapshot.getId());
                            }
                        }
                    }
                });
    }
    private void getUserData() {
        firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                .document(auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        dialogAddPostBinding = DialogAddPostBinding.inflate(inflater, container, false);
        return dialogAddPostBinding.getRoot();
    }
}
