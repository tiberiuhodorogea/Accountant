package com.mainpack.accountant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {


    public void onNext(View view) {
        Intent intent = new Intent(this, InsertParticipants.class);

        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        String event_name = editText.getText().toString();
        if(event_name.trim().length()<3)
        {
            Utils.ErrMsg(view,"At least 3 characters");
            return;
        }

        intent.putExtra("event_name", event_name);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}