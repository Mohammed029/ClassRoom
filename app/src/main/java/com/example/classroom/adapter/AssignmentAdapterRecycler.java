package com.example.classroom.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.R;
import com.example.classroom.model.Assignment;
import com.example.classroom.model.User;
import com.example.classroom.ui.HomeActivity;
import com.example.classroom.ui.TestUpload;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignmentAdapterRecycler extends RecyclerView.Adapter<AssignmentAdapterRecycler.ViewHolder> {
    private Assignment assignment;
    public  static Uri uripdf;
    public ArrayList<Assignment> assignmentsList = new ArrayList<>();
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private User user;

    private FirebaseAuth auth;

    private static HomeActivity homeActivity;

    public AssignmentAdapterRecycler(ArrayList<Assignment> assignmentsList) {
        this.assignmentsList = assignmentsList;

    }

    public static AssignmentAdapterRecycler Method() {
        AssignmentAdapterRecycler adapterRecycler = new AssignmentAdapterRecycler(Method().assignmentsList);
        return adapterRecycler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        storageReference = FirebaseStorage.getInstance().getReference();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_assignment, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentsList.get(position);
        auth = FirebaseAuth.getInstance();
        holder.tvText.setText(assignment.getPostBody());
        holder.tvTime.setText(assignment.getPostTime().toString());
        holder.tvUserName.setText(assignment.getUserTName());
        holder.tvFileName.setText("NameFile");

        FirebaseFirestore.getInstance().collection(Constant.KEY_COLLECTION_USERS)
                .document(assignment.getUserTId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // user.class // حولي اياه كاوبجكت من دكيومنت سنابشوت وخزنه في يوزر
                        //User user = documentSnapshot.toObject(User.class);
                        String image = documentSnapshot.getString("image");
                        if (!image.isEmpty()) {
                            Picasso.get().load(image).into(holder.imgUser);
                        } else {
                            Picasso.get().load(R.drawable.ic_user_avatar).into(holder.imgUser);
                        }
                    }
                });
        holder.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, TestUpload.class));
            }
        });
    }
    private void getAssignment() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return assignmentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private TextView tvUserName, tvTime, tvText, tvFileName;
        private Button btnAttach;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvText = itemView.findViewById(R.id.tvText);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            btnAttach = itemView.findViewById(R.id.btnAttach);

        }
    }
}