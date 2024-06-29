package com.abhay.universityapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.activity.EditProfileActivity;
import com.abhay.universityapplication.activity.SettingsActivity;
import com.abhay.universityapplication.adapter.HomeAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    CircleImageView profileImage;
    TextView name, collegeId, email,settings ,occupation;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    List<Events> events;
    HomeAdapter homeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.proName);
        profileImage = view.findViewById(R.id.proImage);
        collegeId = view.findViewById(R.id.collegeIdValue);
        email = view.findViewById(R.id.emailValue);
        occupation = view.findViewById(R.id.OccupationValue);
        settings = view.findViewById(R.id.profileSettings);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.profileFragmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        readProfile(firebaseUser.getUid());
        events = new ArrayList<Events>();
        readEvents(firebaseUser.getUid());

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return  view;
    }

    public void readProfile(final String userId)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getProfileImage().equals("default"))
                    profileImage.setImageResource(R.drawable.male);
                else
                    Glide.with(getContext()).load(user.getProfileImage()).into(profileImage);
                name.setText(user.getFullname());
                collegeId.setText(user.getStudentId());
                occupation.setText(user.getType());
                email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void readEvents (final String userId)
    {
        events.clear();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    Events event = dataSnapshot1.getValue(Events.class);
                    if(event.getUserId().equals(userId))
                    {
                        events.add(event);
                    }

                }
                Collections.reverse(events);
                homeAdapter = new HomeAdapter(getContext(),events,firebaseUser.getUid());
                recyclerView.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
