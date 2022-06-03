package com.example.classroom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.R;
import com.example.classroom.model.Post;
import com.example.classroom.utils.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapterRecycler extends RecyclerView.Adapter<PostAdapterRecycler.ViewHolder> {

    private List<Post> postList = new ArrayList<>();

    public PostAdapterRecycler(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvText.setText(post.getPostBody());
        holder.tvTime.setText(post.getPostTime().toString());
        holder.tvUserName.setText(post.getUserTName());




        FirebaseFirestore.getInstance().collection(Constant.KEY_COLLECTION_USERS)
                .document(post.getUserTId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //User user = documentSnapshot.toObject(User.class);
                        String image = documentSnapshot.getString("image");
                        if(!image.isEmpty()){
                            Picasso.get().load(image).into(holder.imgUser);
                        }else {
                            Picasso.get().load(R.drawable.ic_user_avatar).into(holder.imgUser);
                        }

                    }
                });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgUser;
        private TextView tvUserName , tvTime, tvText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvText = itemView.findViewById(R.id.tvText);

        }
    }
}
