package com.abhay.universityapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.activity.MessageActivity;
import com.abhay.universityapplication.model.Chat;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private Context context;
    private List<User> users;
    private boolean isChat;
    String thelastmessage;

    public ChatAdapter(Context context, List<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_display2,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getFullname());
        if(user.getProfileImage().equals("default"))
        {
            holder.profile.setImageResource(R.drawable.male);
        }
        else {
            Glide.with(context.getApplicationContext()).load(user.getProfileImage()).into(holder.profile);
        }
        if(isChat)
        {
            lastMessge(user.getId(),holder.lastmessage);
        }
        else
        {
            holder.lastmessage.setVisibility(View.INVISIBLE);
        }

        if(isChat)
        {
            if(user.getStatus().equals("online"))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.img_on.setVisibility(View.INVISIBLE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            holder.img_on.setVisibility(View.INVISIBLE);
            holder.img_off.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void lastMessge(final String userid, final TextView lastmessage)
    {
        thelastmessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Chat chat = dataSnapshot1.getValue(Chat.class);
                    if(chat.getMsgReceiver().equals(firebaseUser.getUid() )&& chat.getMsgSender().equals(userid)
                            || chat.getMsgSender().equals(firebaseUser.getUid() )&& chat.getMsgReceiver().equals(userid))
                    {
                        thelastmessage= chat.getMessage();

                    }
                }
                switch (thelastmessage)
                {
                    case "default":
                        lastmessage.setText("No message");
                        break;
                    default:
                        lastmessage.setText(thelastmessage);
                }
                thelastmessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public CircleImageView profile;
        public CircleImageView img_off;
        public CircleImageView img_on;
        public TextView lastmessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.proUsername);
            profile=itemView.findViewById(R.id.userProfileImage);
            img_off=itemView.findViewById(R.id.img_off);
            img_on=itemView.findViewById(R.id.img_on);
            lastmessage=itemView.findViewById(R.id.lastMessage);
        }
    }
}
