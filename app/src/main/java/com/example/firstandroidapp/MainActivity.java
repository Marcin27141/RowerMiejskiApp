package com.example.firstandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioGroup roleRadioGroup;
    private ProgressBar progressBar;
    private RelativeLayout resultLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.language_menu) {
            Toast.makeText(this, "Language", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

        progressBar = findViewById(R.id.progressBar);
        resultLayout = findViewById(R.id.resultLayout);
    }

    public void onBtnClick(View view) {
        EditText firstNameTxt = findViewById(R.id.firstNameTxt);
        EditText lastNameTxt = findViewById(R.id.lastNameTxt);
        EditText emailTxt = findViewById(R.id.emailTxt);

        TextView firstNameResult = findViewById(R.id.firstNameResult);
        TextView lastNameResult = findViewById(R.id.lastNameResult);
        TextView emailResult = findViewById(R.id.emailResult);

        resultLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            firstNameResult.setText(firstNameTxt.getText());
            lastNameResult.setText(lastNameTxt.getText());
            emailResult.setText(emailTxt.getText());
            resultLayout.setVisibility(View.VISIBLE);
        }, 1000);

        //Toast.makeText(this, String.format("%s %s, %s", firstNameTxt.getText(), lastNameTxt.getText(), emailTxt.getText()), Toast.LENGTH_SHORT).show();
    }
}