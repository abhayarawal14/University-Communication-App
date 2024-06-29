package com.abhay.universityapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.adapter.MessageAdapter;
import com.abhay.universityapplication.model.Chat;
import com.abhay.universityapplication.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView tvusername;
    List<Chat> chats;
    FirebaseUser firebaseUser;
    DatabaseReference refrences;
    private String userid;
    MessageAdapter messageAdapter;
    Intent intent;
    RecyclerView recyclerView;
    ImageButton btnSendMessage,btnSendImage;
    EditText texmessage;
    private StorageTask storageTask;
    private StorageReference storageReference;
    private Uri fileUri;
    boolean notify = false;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        profile_image= findViewById(R.id.profile_image);
        tvusername=findViewById(R.id.username);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnSendMessage=findViewById(R.id.btnsend);
        btnSendImage=findViewById(R.id.btnSelectImage);
        texmessage=findViewById(R.id.sendmessage);
        progressBar = findViewById(R.id.progressBar);
        intent=getIntent();
        userid = intent.getStringExtra("userid");
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message= texmessage.getText().toString();
                if(!message.equals(""))
                {
                    sendMessage(firebaseUser.getUid(),userid,message);
                    texmessage.setText("");
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "There was error sending the message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("MessageImages");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        refrences= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                tvusername.setText(user.getFullname());
                if(user.getProfileImage().equals("Default"))
                {
                    profile_image.setImageResource(R.drawable.male);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(),userid,user.getProfileImage());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                openImage();
            }
        });

    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK && data != null )
        {
            fileUri =data.getData();
            uploadImage();
        }


    }
    private void uploadImage()
    {
        if(fileUri !=null)
        {
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(fileUri));
            storageTask =fileReference.putFile(fileUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("msgSender",firebaseUser.getUid());
                        hashMap.put("msgReciver",userid);
                        hashMap.put("message",mUri);
                        hashMap.put("type","image");
                        reference.child("Chat").push().setValue(hashMap);
                        progressBar.setVisibility(View.GONE);
                        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                .child(userid);
                        chatRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists())
                                {
                                    chatRef.child("id").setValue(firebaseUser.getUid());
                                    chatRef.child("reciverid").setValue(userid);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                .child(firebaseUser.getUid());
                        chatRefReceiver.child("id").setValue(userid);
                        chatRefReceiver.child("reciverid").setValue(firebaseUser.getUid());
                    }
                    else
                    {
                        Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }
    private void sendMessage(final String sender, final String reciver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msgSender",sender);
        hashMap.put("msgReceiver",reciver);
        hashMap.put("message",message);
        hashMap.put("type","text");
        reference.child("Chat").push().setValue(hashMap);

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRefReceiver.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final String msg = message;
        DatabaseReference notific = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        notific.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify)
                {
                }

                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void readMessage(final String myid, final String userid, final String imgUrl)
    {
        chats = new ArrayList<>();
        refrences = FirebaseDatabase.getInstance().getReference("Chat");
        refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getMsgReceiver().equals(userid) && chat.getMsgSender().equals(myid)||
                            chat.getMsgReceiver().equals(myid) && chat.getMsgSender().equals(userid))
                    {
                        chats.add(chat);
                    }


                }
                messageAdapter = new MessageAdapter(getApplicationContext(),chats,imgUrl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void status(String status)
    {
        refrences = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        refrences.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}