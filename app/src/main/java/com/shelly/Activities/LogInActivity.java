package com.shelly.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shelly.R;
import com.shelly.Utils.FirebaseMethods;

public class LogInActivity extends AppCompatActivity {

    //Views
    ImageButton mBackBtn;
    Button mLogInBtn;
    private EditText mEmailET;
    private EditText mPasswordET;
    Button mForgotPasswordBtn;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Firebase Binding
        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(this);

        //Views Binding
        mBackBtn = (ImageButton) findViewById(R.id.BackImageButton);
        mLogInBtn = (Button) findViewById(R.id.LogInButton);
        mEmailET = (EditText) findViewById(R.id.EmailEditText);
        mPasswordET = (EditText) findViewById(R.id.PasswordEditText);
        mForgotPasswordBtn = (Button) findViewById(R.id.ForgotPasswordButton);

        //Implementing Functionalities
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email =  mEmailET.getText().toString();
                String Password = mPasswordET.getText().toString();
                if(TextUtils.isEmpty(Email)) {
                    Toast.makeText(LogInActivity.this, "Please enter email address", Toast.LENGTH_SHORT).show();
                    mEmailET.setError("Required");
                    return;
                }
                else if(TextUtils.isEmpty(Password)) {
                    Toast.makeText(LogInActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Authentification failed", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if(mAuth.getCurrentUser().isEmailVerified()) {
                                    mFirebaseMethods.checkAccountSettingsCompletion();
                                    Intent i = new Intent(LogInActivity.this, MainActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(LogInActivity.this, "Email is not verified. Check your email inbox.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            } catch (NullPointerException e) {
                                Log.e("Exception", "" + e);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
