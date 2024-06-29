package com.abhay.universityapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.abhay.universityapplication.R;

public class ChooseRegisterActivity extends AppCompatActivity {
    LinearLayout userLinear, developerLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_register);
        userLinear= findViewById(R.id.UserLinear);
        developerLinear= findViewById(R.id.DeveloperLinear);

        userLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("As","Student");
                startActivity(intent);
            }
        });
        developerLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                intent.putExtra("As","Teacher");
                startActivity(intent);
            }
        });
    }
}