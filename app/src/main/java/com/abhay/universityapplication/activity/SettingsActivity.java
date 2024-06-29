package com.abhay.universityapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.model.Chat;
import com.abhay.universityapplication.model.Events;
import com.abhay.universityapplication.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    CardView UpdateProfile, DeleteProfile,ClassSchedule, Logout;
    CircleImageView profileImage;
    TextView profileEmail;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        UpdateProfile= findViewById(R.id.cardEditProfile);
        DeleteProfile= findViewById(R.id.cardDeleteProfile);
        ClassSchedule= findViewById(R.id.cardClassSchedule);
        Logout = findViewById(R.id.cardLogout);
        profileImage= findViewById(R.id.settingUserProfile);
        profileEmail= findViewById(R.id.settingUserEmail);
        progressBar= findViewById(R.id.settingProgressBar);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        loadUser(firebaseUser.getUid());

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EditProfileActivity.class);
                startActivity(intent);
            }
        });
        DeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting the account means your all data will be removed from the system");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query eventQuery = ref.child("Events");
                        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                {
                                    Events events = dataSnapshot.getValue(Events.class);
                                    if(events.getUserId().equals(firebaseUser.getUid()))
                                    {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query chatListQuery = ref.child("ChatList");
                        chatListQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                {
                                    if(dataSnapshot.getKey().equals(firebaseUser.getUid()))
                                    {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Query chatlistQuery = ref.child("Chatlist");
                        chatlistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                {
                                    if(dataSnapshot.getKey().equals(firebaseUser.getUid()))
                                    {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query chatQuery = ref.child("Chat");
                        chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    Chat chat = dataSnapshot.getValue(Chat.class);
                                    if (chat.getMsgReceiver().equals(firebaseUser.getUid()) || chat.getMsgSender().equals(firebaseUser.getUid()))
                                    {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {

//                                    Query userQuery = ref.child("Users").child(firebaseUser.getUid());
//                                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            snapshot.getRef().removeValue();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
                                    Toast.makeText(getApplicationContext(), "Account has been deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

    }

    private void loadUser(String userid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getProfileImage().equals("default")){
                    profileImage.setImageResource(R.drawable.male);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profileImage);
                }
                profileEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}