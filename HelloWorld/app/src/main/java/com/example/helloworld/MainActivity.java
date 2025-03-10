package com.example.helloworld;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void goToProfilActivity(){
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(Intent);
    }

    private void showProfileName(){
        SharedPreferences sharedPrefs = getSharedPrefrences("ProfileActivity", Context.MODE_PRIVATE);
        String name = sharedPrefs.getString("ProfileName", null);

        if(name == null){
            goToProfileActivity();

        }
    }
}