package com.abhay.universityapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.abhay.universityapplication.R;
import com.abhay.universityapplication.model.Comment;
import com.abhay.universityapplication.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<Comment> comments;
    Context context;
    public CommentAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_display,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.comment.setText(comment.getComment());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(comment.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.username.setText(user.getFullname());
                if (user.getProfileImage().equals("default"))
                {
                    holder.circleImageView.setImageResource(R.drawable.male);
                }
                else {
                    Glide.with(context.getApplicationContext()).load(user.getProfileImage()).into(holder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView comment, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.comment_profile_image);
            comment = itemView.findViewById(R.id.show_comment);
            username = itemView.findViewById(R.id.comment_username);
        }
    }
}
