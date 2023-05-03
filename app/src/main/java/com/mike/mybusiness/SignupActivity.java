package com.mike.mybusiness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText email, password;
    FirebaseAuth firebaseAuth;
    TextView errorSignup;
    String number;

    ActivityResultLauncher<Intent> activityResultLauncher;
    GoogleSignInClient googleSignInClient;
    SignInButton signInButton;
    Button button;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null)
        {
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        errorSignup = findViewById(R.id.errorsignup);
        button = findViewById(R.id.button5);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        linearLayout = findViewById(R.id.linear);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        signInButton = findViewById(R.id.signinButton);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("385942005873-j9isbne8t2ee30fl1fmhslvvbc832k8p.apps.googleusercontent.com").
                    requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        activityResultLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        if (googleSignInAccountTask.isSuccessful())
                        {
                            try {
                                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                                if (googleSignInAccount != null)
                                {
                                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                                    firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                            finish();
                                        }
                                        else {
                                            showSnack("Failed to sign in try again", true);
                                        }
                                    });
                                }
                            } catch (ApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            showSnack("Failed to sign in try again", true);
                        }
                    }
                    else {
                        showSnack("Failed to sign in try again", true);
                    }
                });
        if (firebaseUser != null)
        {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        }
        else {
            new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Application requires valid internet connection to perform its actions")
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    }).setNegativeButton("Abandon", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setCancelable(false).show().getWindow().setDimAmount(0.5f);
        }
    }

    public void signup(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(emailText))
        {
            email.setError("Enter Email");
        }
        else if (TextUtils.isEmpty(passwordText) || passwordText.length() <= 7)
        {
            password.setError("Enter 8 Character Password");
        }  else {
            firebaseAuth.fetchSignInMethodsForEmail(emailText).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (!(task.getResult().getSignInMethods().size() == 0))
                    {
                        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    button.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    errorSignup.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    button.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    errorSignup.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    public void passwordForgot(View view) {
        String emailText = email.getText().toString();
        if (TextUtils.isEmpty(emailText))
        {
            email.setError("Enter Email");
        }
        else {
            firebaseAuth.fetchSignInMethodsForEmail(emailText).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (!(task.getResult().getSignInMethods().size() == 0))
                    {
            firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        showSnack("Check Your Email To Reset Password", false);
                    }
                    else {
                        showSnack("Failed to reset password, recheck and try again", true);
                    }
                }
            });
                    }
                    else {
                        showSnack("Account Not Exists", true);
                    }
                }
            });
        }
    }
    void showSnack(String message, boolean error)
    {
        if (error)
        {
            Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).setTextColor(Color.RED).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
        }
        else {
            Snackbar.make(linearLayout, message, Snackbar.LENGTH_SHORT).setTextColor(Color.GREEN).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}