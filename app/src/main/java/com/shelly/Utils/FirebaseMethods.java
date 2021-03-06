package com.shelly.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shelly.Activities.AccountTypeActivity;
import com.shelly.Activities.DomainActivity;
import com.shelly.Activities.FinalSetUpActivity;
import com.shelly.Activities.TestActivity;
import com.shelly.Models.User;
import com.shelly.R;

import java.util.HashMap;

public class FirebaseMethods {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefDatabase;
    private String UserID;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefDatabase = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            UserID = mAuth.getCurrentUser().getUid();
        }
    }

    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(task.isSuccessful()){
                            sendVerificationEmail();
                            UserID = mAuth.getCurrentUser().getUid();
                            addNewUser(email, username, "");
                            Log.e("User Id", UserID);
                        }

                    }
                });
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                        Toast.makeText(mContext, "Couldn't send verification email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addNewUser(String email, String username, String accountType){
        User user = new User(UserID, username, email, accountType);
        mRefDatabase.child(mContext.getString(R.string.dbfield_users))
                .child(UserID)
                .setValue(user);
    }

    public void checkAccountSettingsCompletion() {
        mRefDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String AccountType = (String) dataSnapshot.child(mContext.getString(R.string.dbfield_users)).
                                        child(UserID).
                                            child(mContext.getString(R.string.dbfield_user_accounttype)).
                                                getValue();
                if(TextUtils.isEmpty(AccountType)) {
                    Intent i = new Intent(mContext, AccountTypeActivity.class);
                    mContext.startActivity(i);
                    ((Activity) mContext).finish();
                } else {
                    if(AccountType.equals("Member")) {
                        if(!dataSnapshot.child(mContext.getString(R.string.dbfield_members)).hasChild(UserID)) {
                            Intent i = new Intent(mContext, FinalSetUpActivity.class);
                            mContext.startActivity(i);
                            ((Activity) mContext).finish();
                        }
                    } else if(AccountType.equals("Ambassador")) {
                        if(!dataSnapshot.child(mContext.getString(R.string.dbfield_ambassadors)).hasChild(UserID)) {
                            Intent i = new Intent(mContext, FinalSetUpActivity.class);
                            mContext.startActivity(i);
                            ((Activity) mContext).finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


