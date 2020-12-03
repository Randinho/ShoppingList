package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView name = (TextView) findViewById(R.id.helloTextView);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        String txt = "Hello " + currentUser.getEmail();

        name.setText(txt);


    }

    public void logout(View view){
        mAuth.signOut();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
