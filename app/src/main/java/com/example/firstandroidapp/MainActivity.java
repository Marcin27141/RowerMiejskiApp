package com.example.firstandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioGroup roleRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roleRadioGroup = findViewById(R.id.roleRadioGrp);
        roleRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.isAdminRadioBtn) {
                Toast.makeText(MainActivity.this, "Admin", Toast.LENGTH_SHORT).show();
            } else if (i == R.id.isUserRadioBtn) {
                Toast.makeText(MainActivity.this, "User", Toast.LENGTH_SHORT).show();
            }
        });
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

        Toast.makeText(this, String.format("%s %s, %s", firstNameTxt.getText(), lastNameTxt.getText(), emailTxt.getText()), Toast.LENGTH_SHORT).show();
    }
}