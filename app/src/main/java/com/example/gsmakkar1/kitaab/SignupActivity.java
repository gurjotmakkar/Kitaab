package com.example.gsmakkar1.kitaab;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class SignupActivity extends AppCompatActivity {


    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupDOB;
    private Button signupButton;
    private TextView goToLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        goToLogin = (TextView) findViewById(R.id.signupYesAccountLabel);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginUser = new Intent(getBaseContext(), LoginActivity.class);
                loginUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginUser);
            }
        });

        signupEmail = (EditText) findViewById(R.id.signupEmail);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupDOB = (EditText) findViewById(R.id.signupDob);
        signupButton = (Button) findViewById(R.id.signupSignupButton);

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signupUser();

            }
        });

    }

    private void signupUser() {
        final String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        final String DOB = signupDOB.getText().toString().trim();
        int day = Integer.getInteger(DOB.substring(0, 1));
        int month = Integer.getInteger(DOB.substring(2, 3));
        int year = Integer.getInteger(DOB.substring(4, DOB.length()));


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(DOB)) {

            mProgress.setMessage("Signing up...");
            mProgress.show();
            if (calculateAge(year, month, day) <= 9) {
                if (password.length() >= 8) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String userID = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUserDb = mDatabase.child(userID);

                                currentUserDb.child("DOB").setValue(DOB);
                                currentUserDb.child("email").setValue(email);
                                currentUserDb.child("username").setValue(email.substring(0, email.indexOf("@")));

                                mProgress.dismiss();

                                Intent goToMain = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(goToMain);

                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Password should be 8 or more characters", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Minimum age requirement in 10 and over", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    private int calculateAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }
}

