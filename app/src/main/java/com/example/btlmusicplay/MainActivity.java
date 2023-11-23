package com.example.btlmusicplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String USER_ID = "user_id";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_SEX = "user_sex";
    public static final String USER_FULL_NAME = "user_full_name";
    public static final String MUSIC_SELECT = "music";
    public static final String SHUFFLE_PLAY= "shuffle_play";

    public static final String LOOP_PLAY= "loop_play";

//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent myIntent = new Intent(this, LoginActivity.class);
        Intent intentMainToHome = new Intent(this, HomeActivity.class);
        this.startActivity(intentMainToHome);
        finish();


    }
}