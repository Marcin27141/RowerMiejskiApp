package com.example.firstandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onBtnClick(View view) {
        EditText firstNameTxt = findViewById(R.id.firstNameTxt);
        EditText lastNameTxt = findViewById(R.id.lastNameTxt);
        EditText emailTxt = findViewById(R.id.emailTxt);

        TextView firstNameResult = findViewById(R.id.firstNameResult);
        TextView lastNameResult = findViewById(R.id.lastNameResult);
        TextView emailResult = findViewById(R.id.emailResult);

        firstNameResult.setText(firstNameTxt.getText());
        lastNameResult.setText(lastNameTxt.getText());
        emailResult.setText(emailTxt.getText());
    }
}