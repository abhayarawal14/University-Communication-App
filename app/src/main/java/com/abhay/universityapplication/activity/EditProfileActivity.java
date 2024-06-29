package com.abhay.universityapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhay.universityapplication.R;
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

public class EditProfileActivity extends AppCompatActivity {
    CircleImageView imageView;
    EditText collegeId, name, email,password,confirmPassword;
    Button btnUpdate;
    FirebaseUser firebaseUser;
    Spinner batch;
    TextView tvBatch;
    StorageReference storageReference;
    private String type;
    private Uri imageURl;
    private StorageTask<UploadTask.TaskSnapshot> uploadsTask;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        imageView = findViewById(R.id.updateProfileImage);
        name = findViewById(R.id.updateName);
        collegeId= findViewById(R.id.updateCollegeId);
        email = findViewById(R.id.updateEmail);
        password = findViewById(R.id.updatePassword);
        confirmPassword = findViewById(R.id.updateConfirmPassword);
        progressBar =findViewById(R.id.updateProgressbar);
        batch= findViewById(R.id.updateBatchSpinner);
        btnUpdate = findViewById(R.id.btnUpdateProfile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        tvBatch= findViewById(R.id.updateTvBatch);
        String arrayBatch[] = {"21A", "21B", "21C", "22A", "22B", "22C", "22D", "23A", "23B", "24A", "24B", "24C", "25A", "25B", "25C", "26A", "26B"};
        ArrayAdapter batchAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayBatch);
        batch.setAdapter(batchAdapter);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getFullname());
                email.setText(user.getEmail());
                collegeId.setText(user.getStudentId());
                password.setText(user.getPassword());
                type= user.getType();
                if(user.getType().equals("Teacher"))
                {
                    batch.setVisibility(View.GONE);
                    tvBatch.setVisibility(View.GONE);
                }
                for(int i= 0; i < batch.getAdapter().getCount(); i++)
                {
                    if(batch.getAdapter().getItem(i).toString().contains(user.getBatch()))
                    {
                        batch.setSelection(i);
                    }
                }
                if(user.getProfileImage().equals("default"))
                {
                    imageView.setImageResource(R.drawable.male);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(confirmPassword.getText().toString()))
                {

                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    if(type.equals("Teacher"))
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("email",email.getText().toString());
                        hashMap.put("fullname",name.getText().toString());
                        hashMap.put("studentId",collegeId.getText().toString());
                        hashMap.put("password",password.getText().toString());
                        databaseReference.updateChildren(hashMap);
                    }
                    else
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("email",email.getText().toString());
                        hashMap.put("fullname",name.getText().toString());
                        hashMap.put("studentId",collegeId.getText().toString());
                        hashMap.put("password",password.getText().toString());
                        hashMap.put("batch",batch.getSelectedItem().toString());
                        databaseReference.updateChildren(hashMap);
                    }

                    uploadImage();

                    name.setText("");
                    confirmPassword.setText("");
                    password.setText("");
                    collegeId.setText("");
                    email.setText("");
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                }


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
            imageURl =data.getData();
            imageView.setImageURI(imageURl);
        }
    }


    private void uploadImage()
    {

        if(imageURl !=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageURl));
            uploadsTask =fileReference.putFile(imageURl);
            uploadsTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
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

                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    if (task.isSuccessful())
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("profileImage",mUri);
                        reference.child("Users").child(firebaseUser.getUid()).updateChildren(hashMap);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(EditProfileActivity.this, "Profile Uploaded", Toast.LENGTH_SHORT).show();
        }

    }
}