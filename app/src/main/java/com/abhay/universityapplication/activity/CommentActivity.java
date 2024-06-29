package com.abhay.universityapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.adapter.CommentAdapter;
import com.abhay.universityapplication.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    TextView like, noComments;
    EditText commentBox;
    ImageView btnLike,sendComment;
    RecyclerView commentRecyclerView;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        final String postId = getIntent().getStringExtra("PostId");
        like= findViewById(R.id.textViewLikeCount);
        btnLike= findViewById(R.id.btnLike2);
        noComments= findViewById(R.id.NoComments);
        commentBox= findViewById(R.id.commentBox);
        sendComment= findViewById(R.id.sendComment);
        commentRecyclerView= findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        readLikes(postId);
        readComments(postId);
        CheckLike(postId,btnLike);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComments(postId);
            }
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnLike.getTag().equals("Like"))
                {
                    FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Like").child(firebaseUser.getUid()).setValue(true);

                }
                else if (btnLike.getTag().equals("Liked"))
                {
                    FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Like").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }
    private void readLikes(String postId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Like");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                like.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readComments(String PostId)
    {
        commentRecyclerView.setVisibility(View.VISIBLE);
        noComments.setVisibility(View.GONE);
        List<Comment> comments = new ArrayList<>();
        comments.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(PostId).child("Comment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    comments.add(comment);
                }
                Collections.reverse(comments);
                if (comments.size()==0)
                {
                    commentRecyclerView.setVisibility(View.GONE);
                    noComments.setVisibility(View.VISIBLE);

                }
                else
                {
                    CommentAdapter commentAdapter = new CommentAdapter(comments,getApplicationContext());
                    commentRecyclerView.setAdapter(commentAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendComments(String postId)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Comment");
        String commentId = reference.push().getKey();
        String comment = commentBox.getText().toString();
        if (comment.equals(""))
        {
            Toast.makeText(this, "Comment box is empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("id",commentId);
            hashMap.put("comment",comment);
            hashMap.put("userId",firebaseUser.getUid());
            reference.push().setValue(hashMap);

            Toast.makeText(this, "Comment send", Toast.LENGTH_SHORT).show();
            commentBox.setText("");
        }

    }
    private void CheckLike(final String eventId, final ImageView imageView  ) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(eventId).child("Like");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_up_blue_alt_24);
                    imageView.setTag("Liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                    imageView.setTag("Like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}