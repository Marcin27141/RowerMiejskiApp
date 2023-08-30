package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends MenuBarActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGrp);
        roleRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.isAdminRadioBtn) {
                Toast.makeText(MainActivity.this, R.string.admin_radio_btn, Toast.LENGTH_SHORT).show();
            } else if (i == R.id.isUserRadioBtn) {
                Toast.makeText(MainActivity.this, R.string.user_radio_btn, Toast.LENGTH_SHORT).show();
            }
        });

        progressBar = findViewById(R.id.progressBar);
    }

    public void onRegisterClicked(View view) {
        EditText firstNameTxt = findViewById(R.id.firstNameTxt);
        EditText lastNameTxt = findViewById(R.id.lastNameTxt);
        EditText emailTxt = findViewById(R.id.emailTxt);

        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, RegisterResultsActivity.class);
            intent.putExtra("FIRST_NAME", firstNameTxt.getText().toString());
            intent.putExtra("LAST_NAME", lastNameTxt.getText().toString());
            intent.putExtra("EMAIL", emailTxt.getText().toString());
            startActivity(intent);
            finish();
        }, 1000);
    }
}