package com.example.dotslash.dotslashhome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void tryLogIn(View view) {
        Intent intent = new Intent(this, DisplayDash.class);
        String user, pass;
        user = ((EditText)findViewById(R.id.editText)).getText().toString();
        pass = ((EditText)findViewById(R.id.editText2)).getText().toString();
        intent.putExtra("username", user);
        intent.putExtra("password", pass);
        startActivity(intent);
    }
}
//private Button logIn;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logIn  = (Button) findViewById(R.id.logIn);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText user, pass;
                user = (EditText) findViewById(R.id.editText);
                pass = (EditText) findViewById(R.id.editText2);
                start(user.getText().toString(), pass.getText().toString());
            }
        });
    }

    */
