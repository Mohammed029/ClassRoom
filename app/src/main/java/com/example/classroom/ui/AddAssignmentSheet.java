package com.example.classroom.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.classroom.databinding.BotomSheetFragmentAddAssignemtBinding;
import com.example.classroom.model.Assignment;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddAssignmentSheet extends BottomSheetDialogFragment {
    private BotomSheetFragmentAddAssignemtBinding binding;
    private FirebaseFirestore firebaseFirestore;
    List<String> stList = new ArrayList<>();
    private String name= "";
    /* public static AddAssignmentSheet newInstance() {
         AddAssignmentSheet fragment = new AddAssignmentSheet();
         return fragment;
     }*/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task!=null && task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                stList.add(snapshot.getId());
                            }
                        }
                    }
                });
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        firebaseFirestore.collection(Constant.KEY_COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString(Constant.KEY_USER_NAME);
                    }
                });
        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        Date date = new Date();
        DocumentReference documentReference = firebaseFirestore.collection(Constant.KEY_COLLECTION_ASSIGNMENT).document();
        String docId=  documentReference.getId();


        binding.btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                String text = binding.edtPostMessage.getText().toString();
                Assignment assignment = new Assignment(docId,
                        text, date, uid, name,stList);

                documentReference.set(assignment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);
                        getDialog().dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);

                        Log.d("TAG", e.getMessage());
                    }
                });
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BotomSheetFragmentAddAssignemtBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
