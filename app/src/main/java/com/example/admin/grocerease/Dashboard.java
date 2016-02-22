package com.example.admin.grocerease;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends ActionBarActivity {

    Spinner memberSpinner;
    private ArrayList<String> members;
    TextView welcome, fam, none;
    ListView groceryList;
    ImageButton add;
    SimpleAdapter adapterL;
    List<Map<String, String>> groceryData;
    boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        groceryList = (ListView)findViewById(R.id.listView);

        final Firebase myFirebaseRef = new Firebase("https://grocerease.firebaseio.com/");
        AuthData user = myFirebaseRef.getAuth();
        final String id = user.getUid();

        memberSpinner = (Spinner) findViewById(R.id.spinner);

        members = new ArrayList<String>();

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object famName = snapshot.child("users").child(id).child("familyName").getValue();
                fam.setText(famName.toString());

                ArrayList<String> arr = (ArrayList<String>) snapshot.child("users").child(id).child("members").getValue();
                setSpinner(arr);

                final Firebase grocerRef = new Firebase("https://grocerease.firebaseio.com/groceries");
                final ArrayList<String> arr2 = (ArrayList<String>) snapshot.child("users").child(id).child("groceryList").getValue();
                grocerRef.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot gsnapshot) {
                        List<Map<String, String>> groceryData = new ArrayList<Map<String, String>>();

                        if (!loaded) {
                            if (arr2 != null) {
                                for (String s : arr2) {
                                    Map<String, String> datum = new HashMap<String, String>(2);
                                    datum.put("title", gsnapshot.child(s).child("name").getValue().toString());
                                    datum.put("qty", "x" + gsnapshot.child(s).child("quantity").getValue().toString());
                                    groceryData.add(datum);
                                }
                                setGroceryList(groceryData);
                                loaded = true;
                            } else none.setVisibility(View.VISIBLE);
                        }

                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        welcome = (TextView)findViewById(R.id.welcome);
        fam = (TextView)findViewById(R.id.fam);
        fam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Settings.class);
                startActivityForResult(intent, 1);
            }
        });
        none = (TextView)findViewById(R.id.none);


        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewItem.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void setGroceryList(List<Map<String, String>> groceryData) {
        adapterL = new SimpleAdapter(Dashboard.this, groceryData, android.R.layout.simple_list_item_2,
                new String[]{"title", "qty"},
                new int[]{android.R.id.text1, android.R.id.text2});
        groceryList.setAdapter(adapterL);
        groceryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                final int pos = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Dashboard.this);
                alertDialogBuilder.setMessage("Delete item");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final Firebase myRef = new Firebase("https://grocerease.firebaseio.com/users");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot snapshot) {
                                AuthData user = myRef.getAuth();
                                final String id = user.getUid();
                                ArrayList<String> groceries = (ArrayList<String>) snapshot.child(id)
                                        .child("groceryList").getValue();
                                String gId = groceries.remove(pos);
                                final Firebase gRef = new Firebase("https://grocerease.firebaseio.com/groceries");
                                gRef.child(gId).setValue(null);
                                myRef.child(id).child("groceryList").setValue(groceries);
                            }

                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void setSpinner(ArrayList<String> arr) {
        for (String s: arr) {
            members.add(s);
        }
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_spinner_item, members);
        memberSpinner.setAdapter(adapterS);
        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void updateView() {
        welcome.setText("Hello, " + memberSpinner.getSelectedItem().toString() + "!");
    }

}
