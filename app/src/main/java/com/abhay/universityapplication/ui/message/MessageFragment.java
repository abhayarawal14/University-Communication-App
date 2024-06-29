package com.abhay.universityapplication.ui.message;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.adapter.ChatAdapter;
import com.abhay.universityapplication.model.ChatList;
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

public class MessageFragment extends Fragment {


    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    List<ChatList> userList;
    DatabaseReference reference;
    private List<User> mUsers;
    ChatAdapter chatAdapter ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()) ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
                ReaChat();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void ReaChat() {
        mUsers = new ArrayList<>();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    User user = dataSnapshot1.getValue(User.class);
                    for(ChatList chatList: userList) {

                        if(user.getId().equals(chatList.getId())){
                            mUsers.add(user);
                        }


                    }


                }
                chatAdapter = new ChatAdapter(getContext(),mUsers ,true);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}