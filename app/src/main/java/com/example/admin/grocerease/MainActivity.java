package com.example.admin.grocerease;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button reg, login;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        reg = (Button)findViewById(R.id.reg);
        reg.setOnClickListener(this);

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(this);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        if (v == reg) {
            Intent myIntent = new Intent(this, Registration.class);
            startActivity(myIntent);
        }
        if (v == login) {
            login();
        }
    }

    public void login() {

        final Firebase myFirebaseRef = new Firebase("https://grocerease.firebaseio.com/");

        myFirebaseRef.authWithPassword(username.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

