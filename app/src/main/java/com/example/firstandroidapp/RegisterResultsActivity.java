package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterResultsActivity extends MenuBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_result);

        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");
        String email = getIntent().getStringExtra("EMAIL");

        TextView firstNameTxt = findViewById(R.id.firstNameResult);
        TextView lastNameTxt = findViewById(R.id.lastNameResult);
        TextView emailTxt = findViewById(R.id.emailResult);

        firstNameTxt.setText(firstName);
        lastNameTxt.setText(lastName);
        emailTxt.setText(email);
    }
}