package com.abhay.universityapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.activity.CommentActivity;
import com.abhay.universityapplication.model.Events;
import com.abhay.universityapplication.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Viewholder> {
    private Context myContext;
    private List<Events> events;
    private String uid;
    boolean like = false;
    FirebaseUser firebaseUser;



    public HomeAdapter(Context myContext, List<Events> events, String uid) {
        this.myContext = myContext;
        this.events = events;
        this.uid = uid;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.user_display,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Events event = events.get(position);
        if(event.getType().equals("Announcement")){
            holder.content.setText(" "+event.getContent());
            holder.content.setCompoundDrawablesWithIntrinsicBounds(R.drawable.loudspeaker, 0, 0, 0);
        }
        else {
            holder.content.setText(event.getContent());
        }
        CheckLike(event.getPostId(),holder.like);
        readLikes(event.getPostId(),holder.likeText);
        CountComments(event.getPostId(),holder.commentText);
        if (isValidContextForGlide(myContext)){
            if(event.getEventImage().equals("Blank"))
            {
                holder.imageView.setVisibility(View.GONE);
            }
            else {
                holder.imageView.setVisibility(View.VISIBLE);
                Glide.with(myContext).load(event.getEventImage()).into(holder.imageView);

                holder.userProfileImage.setImageResource(R.mipmap.ic_launcher);

            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(event.getUserId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.fullName.setText(user.getFullname());

                    if(user.getProfileImage().equals("default"))
                    {
                        holder.userProfileImage.setImageResource(R.drawable.male);

                    }
                    else
                    {
                        Glide.with(myContext.getApplicationContext()).load(user.getProfileImage()).into(holder.userProfileImage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("Like"))
                {
                    FirebaseDatabase.getInstance().getReference("Activities").child(event.getPostId()).child("Like").child(firebaseUser.getUid()).setValue(true);

                }
                else if (holder.like.getTag().equals("Liked"))
                {
                    FirebaseDatabase.getInstance().getReference("Activities").child(event.getPostId()).child("Like").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myContext, CommentActivity.class);
                intent.putExtra("PostId",event.getPostId());
                myContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        CircleImageView userProfileImage;

        ImageView imageView;
        TextView fullName, content, commentText, country ,likeText;
        ImageView like , comment;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            imageView = itemView.findViewById(R.id.ContentImage);
            fullName = itemView.findViewById(R.id.proUsername);
            content = itemView.findViewById(R.id.eventContent);
            like = itemView.findViewById(R.id.btnLike);
            comment = itemView.findViewById(R.id.btnComment);
            likeText = itemView.findViewById(R.id.likeCountText);
            commentText = itemView.findViewById(R.id.comment_CountText);
        }

    }
    public void readLikes(String postId, final TextView likeText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Like");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeText.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void CountComments(String postId, final TextView commentText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Comment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentText.setText(dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void CheckLike(final String eventId, final ImageView imageView  )
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(eventId).child("Like");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_up_blue_alt_24);
                    imageView.setTag("Liked");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                    imageView.setTag("Like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

}
