package com.example.admin.grocerease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/*import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;*/

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Registration extends ActionBarActivity implements View.OnClickListener {

    Button reg;
    ImageButton add;
    EditText lname, username, password, email, member;
    ArrayList<EditText> members;
    LinearLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Firebase.setAndroidContext(this);

        reg = (Button)(findViewById(R.id.button));
        add = (ImageButton)(findViewById(R.id.add));

        lname = (EditText)(findViewById(R.id.lname));
        username = (EditText)(findViewById(R.id.username));
        password = (EditText)(findViewById(R.id.password));
        email = (EditText)(findViewById(R.id.email));
        member = (EditText)(findViewById(R.id.member));
        parent = (LinearLayout)findViewById(R.id.parent);

        reg.setOnClickListener(this);

        members = new ArrayList<EditText>();
        members.add(member);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addEditView();
            }
        });
    }

    private void addEditView() {
        // TODO Auto-generated method stub
        EditText et = new EditText(this);
        et.setHint("name");
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        LinearLayout container = (LinearLayout)findViewById(R.id.parent);
        container.addView(et);
        et.requestFocus();
        members.add(et);
    }

    public void onClick(View v) {
        createUser();
        finish();
    }

    public void createUser() {
        final Firebase myFirebaseRef = new Firebase("https://grocerease.firebaseio.com/");
        myFirebaseRef.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                myFirebaseRef.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        ArrayList<String> arr = new ArrayList<String>();
                        for (EditText et : members) {
                            arr.add(et.getText().toString());
                        }

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("familyName", lname.getText().toString());
                        map.put("groceryList", new ArrayList<String>());
                        map.put("members", arr);

                        myFirebaseRef.child("users").child(authData.getUid()).setValue(map);
                        Intent intent = new Intent(Registration.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(Registration.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(Registration.this, "Error creating user. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}