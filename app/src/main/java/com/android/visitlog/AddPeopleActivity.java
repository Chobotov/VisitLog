package com.android.visitlog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class AddPeopleActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        textInputLayout = findViewById(R.id.add_textInputLayout);
        findViewById(R.id.add_in_addActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("name",textInputLayout.getEditText().getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

}
