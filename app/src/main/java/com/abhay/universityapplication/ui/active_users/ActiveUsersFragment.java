package com.abhay.universityapplication.ui.active_users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.adapter.UserAdapter;
import com.abhay.universityapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ActiveUsersFragment extends Fragment {


    RecyclerView recyclerView;
    List<User> users;
    FirebaseUser firebaseUser;
    String userData[]=new String[2];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_active_users, container, false);
        recyclerView = view.findViewById(R.id.userListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        seeFriends(firebaseUser.getUid());
        friendsList(userData);
        return  view;
    }

    private String[] seeFriends(String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userData[0]= user.getBatch();
                userData[1]=user.getType();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        return userData;

    }
    private void friendsList(String[] userData)
    {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    User user= dataSnapshot.getValue(User.class);
                    if(user.getId().equals(firebaseUser.getUid()))
                    {
                        continue;
                    }
                    if(userData[1].equals("Student")){
                        if(user.getType().equals("Student"))
                        {
                            if(user.getBatch().equals(userData[0]))
                            {

                                users.add(user);

                            }

                        }
                        else if (user.getType().equals("Teacher"))
                        {
                            users.add(user);
                        }

                    }
                    if(userData[1].equals("Teacher")){
                        users.add(user);
                    }

                }

                UserAdapter userAdapter = new UserAdapter(getContext(),users,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}