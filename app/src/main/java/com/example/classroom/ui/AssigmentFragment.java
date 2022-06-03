package com.example.classroom.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.classroom.R;
import com.example.classroom.adapter.AssignmentAdapterRecycler;
import com.example.classroom.adapter.PostAdapterRecycler;
import com.example.classroom.databinding.FragmentAssigmentBinding;
import com.example.classroom.databinding.FragmentFeedBinding;
import com.example.classroom.model.Assignment;
import com.example.classroom.model.Post;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class AssigmentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Assignment> stList = new ArrayList<>();
    private AssignmentAdapterRecycler assignmentAdapterRecycler;
    private FragmentAssigmentBinding binding;
    private FirebaseFirestore firebaseFirestore;
    public AssigmentFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding = FragmentAssigmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllAssignment();
        binding.refreshLayout.setOnRefreshListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        assignmentAdapterRecycler = new AssignmentAdapterRecycler(stList);
        binding.recyclerView.setAdapter(assignmentAdapterRecycler);
    }
    private void getAllAssignment() {
        firebaseFirestore.collection(Constant.KEY_COLLECTION_ASSIGNMENT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task != null && task.isSuccessful()) {
                            binding.refreshLayout.setRefreshing(false);
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                //Log.d("TagError",snapshot.getData()+"");
                                Assignment assignment = snapshot.toObject(Assignment.class);
                                stList.add(assignment);
                                assignmentAdapterRecycler.notifyDataSetChanged();
                            }
                        }
                        else {
                            binding.refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }
    @Override
    public void onRefresh() {
        stList.clear();
        getAllAssignment();
    }
}