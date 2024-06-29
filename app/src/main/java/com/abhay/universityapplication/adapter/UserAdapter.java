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
import com.abhay.universityapplication.model.User;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> users;
    private boolean isChat;

    public UserAdapter(Context context, List<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.userlist,parent,false);

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



    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public CircleImageView profile;
        public CircleImageView img_off;
        public CircleImageView img_on;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.proUsername);
            profile=itemView.findViewById(R.id.profileList);
            img_off=itemView.findViewById(R.id.img_off);
            img_on=itemView.findViewById(R.id.img_on);
        }
    }
}
