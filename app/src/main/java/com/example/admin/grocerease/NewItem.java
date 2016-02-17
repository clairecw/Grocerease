package com.example.admin.grocerease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.SaveCallback;

public class NewItem extends ActionBarActivity {

    EditText item, quantity, price, desc;
    Button save;
    private boolean updated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        final Firebase myFirebaseRef = new Firebase("https://grocerease.firebaseio.com/");
        AuthData user = myFirebaseRef.getAuth();
        final String userId = user.getUid();

        item = (EditText)findViewById(R.id.item);
        quantity = (EditText)findViewById(R.id.qty);
        price = (EditText)findViewById(R.id.price);
        desc = (EditText)findViewById(R.id.desc);

        save = (Button)findViewById(R.id.add);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // needs checking

                final String id = hashbrowns(10);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", item.getText().toString());
                map.put("quantity", Integer.parseInt(quantity.getText().toString()));
                map.put("maxPrice", Integer.parseInt(price.getText().toString()));
                map.put("description", desc.getText().toString());
                myFirebaseRef.child("groceries").child(id).setValue(map);

                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<String> arr = (ArrayList<String>) snapshot.child("users").child(userId).child("groceryList").getValue();
                        if (arr == null) arr = new ArrayList<String>();
                        arr.add(id);
                        Map<String, Object> groceryList = new HashMap<String, Object>();
                        groceryList.put("groceryList", arr);
                        if (!updated) {
                            myFirebaseRef.child("users").child(userId).updateChildren(groceryList);
                            updated = true;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

                Intent intent = new Intent(NewItem.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Produces yummy, jumbly hash for a random ID. Alphanumeric only.
    private String hashbrowns(int x) {
        String s = "";
        for (int i = 0; i < x; i++) {
            switch ((int)(Math.random() * 3 + 1)) {
                case 1: int n = (int)(Math.random() * 25) + 65;
                        s += Character.toString((char)n);
                        break;
                case 2: s += "" + (int)(Math.random() * 9); break;
                case 3: int k = (int)(Math.random() * 25) + 97;
                        s += Character.toString((char)k);
                        break;
            }
        }
        return s;
    }

}
