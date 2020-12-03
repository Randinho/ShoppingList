package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordEditText);
    }

    public void registerUser(View view){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!email.equals("") && !password.equals(""))
        {
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            //updateUI(user);
                        }
                        else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        else{

            Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
