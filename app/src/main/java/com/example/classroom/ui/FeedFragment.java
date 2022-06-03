package com.example.classroom.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classroom.R;
import com.example.classroom.adapter.PostAdapterRecycler;
import com.example.classroom.databinding.FragmentFeedBinding;
import com.example.classroom.model.Post;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<Post> postList = new ArrayList<>();
    private PostAdapterRecycler postAdapterRecycler;
    FragmentFeedBinding binding;
    private FirebaseFirestore firebaseFirestore;
    public FeedFragment() {
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
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllPost();
        binding.refreshLayout.setOnRefreshListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        postAdapterRecycler = new PostAdapterRecycler(postList);
        binding.recyclerView.setAdapter(postAdapterRecycler);

    }

    private void getAllPost() {
        firebaseFirestore.collection(Constant.KEY_COLLECTION_POST)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task != null && task.isSuccessful()) {
                            binding.refreshLayout.setRefreshing(false);
                            for(DocumentSnapshot snapshot:task.getResult().getDocuments()){
                                Post post = snapshot.toObject(Post.class);
                                postList.add(post);
                                postAdapterRecycler.notifyDataSetChanged();
                            }
                        }else {
                            binding.refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        postList.clear();
        getAllPost();
    }
}