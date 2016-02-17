package com.example.admin.grocerease;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by admin on 2/1/16.
 */
public class User {

    private String lastName;
    private ArrayList<String> members, groceryList;

    public User() {
        lastName = "Last";
        members = new ArrayList<String>();
        groceryList = new ArrayList<String>();
    }

    public User(String lName, ArrayList<String> names) {
        lastName = lName;
        members = names;
        groceryList = new ArrayList<String>();
    }
}
