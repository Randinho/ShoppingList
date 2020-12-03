package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    EditText passwordEditText;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

         emailEditText = (EditText) findViewById(R.id.loginEditText);
         passwordEditText = (EditText) findViewById(R.id.passwordEditText);



    }

    public void loginUser(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                //finish();


                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }
        else{
            Toast.makeText(MainActivity.this, "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void goToRegisterPage(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
