package com.abhay.universityapplication.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abhay.universityapplication.MainActivity;
import com.abhay.universityapplication.R;
import com.abhay.universityapplication.databinding.FragmentDashboardBinding;
import com.abhay.universityapplication.model.User;
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

public class DashboardFragment extends Fragment {

    ImageButton addPhoto;
    ImageView eventImage;
    Spinner spinner,price;
    Button btnPost;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageReference storageReference;
    EditText content;
    private Uri imageURl;
    private StorageTask<UploadTask.TaskSnapshot> uploadsTask;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        addPhoto = view.findViewById(R.id.addPhoto);
        spinner = view.findViewById(R.id.profile_spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Post");
        arrayList.add("Announcement");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        eventImage = view.findViewById(R.id.showEventImage);
        progressBar = view.findViewById(R.id.addProgressBar);
        content = view.findViewById(R.id.addEventContent);
        btnPost = view.findViewById(R.id.btnPostEvent);
        storageReference = FirebaseStorage.getInstance().getReference("EventImage");
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(content.getText().equals(null) && imageURl == null)
                {
                    Toast.makeText(getContext(), "The Event does not have a content", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImage();
                }
            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getType().equals("Teacher")){
                    spinner.setVisibility(View.VISIBLE);
                }
                else {
                    spinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    public String getFileExtension(Uri uri)
    {
        Context applicationContext = MainActivity.getContextOfApplication();
        ContentResolver contentResolver = applicationContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK && data != null )
        {
            imageURl =data.getData();
            eventImage.setVisibility(View.VISIBLE);
            eventImage.setImageURI(imageURl);
        }
    }

    private void uploadImage()
    {
        final String text = content.getText().toString();

        String type = spinner.getSelectedItem().toString();

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
                    String text = content.getText().toString();

                    if (task.isSuccessful())
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
                        String postId = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("content",text);
                        hashMap.put("eventImage",mUri);
                        hashMap.put("type",type);
                        hashMap.put("userId",firebaseUser.getUid());
                        hashMap.put("postId",postId);
                        reference.child(postId).setValue(hashMap);
                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        content.setText("");
                        eventImage.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    else
                    {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String postId = reference.push().getKey();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("postId",postId);
            hashMap.put("content",text);
            hashMap.put("type",type);
            hashMap.put("eventImage","Blank");
            hashMap.put("userId",firebaseUser.getUid());

            reference.child("Events").child(postId).setValue(hashMap);
            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            content.setText("");
            eventImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

    }

}