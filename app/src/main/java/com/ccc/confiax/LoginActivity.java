package com.ccc.confiax;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity {
private TextView maintitle;
private GifImageView mainlogo;
private EditText email, password;
private Button btlogin;

private Button btshowPwd;
private LoginButton btloginFB;
private SignInButton btloginGoogle;
private CallbackManager callbackManager;
private Button signup;
private GoogleSignInClient googleSignInClient;
private FirebaseAuth firebaseAuth;
private FirebaseAuth.AuthStateListener authStateListener;
//private ProgressBar progressBar;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
public static final int SING_IN_CODE = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        maintitle = (TextView) findViewById(R.id.maintitle);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animator1);
        maintitle.startAnimation(animation);

        mainlogo = (GifImageView) findViewById(R.id.logotipo);

        btlogin = (Button) findViewById(R.id.login);

        email = (EditText) findViewById(R.id.email);

        password = (EditText) findViewById(R.id.password);

        btshowPwd = (Button) findViewById(R.id.imgBtShowPwd);

        btloginFB = (LoginButton) findViewById(R.id.lbFacebook);
        btloginFB.setPermissions(Arrays.asList("email", "user_friends", "user_likes"));
        //btloginFB.setAuthType(AUTH_TYPE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btloginGoogle = (SignInButton) findViewById(R.id.lbGoogle);
        btloginGoogle.setBackgroundResource(R.drawable.instabtn);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signup =  (Button) findViewById(R.id.register);

        btloginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getApplicationContext(), R.string.cancel_session, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "facebook:onError", e);
                Toast.makeText(getApplicationContext(), R.string.error_session, Toast.LENGTH_SHORT).show();
            }
        });

        btloginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.login_w_Google, Toast.LENGTH_LONG).show();
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, SING_IN_CODE);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goHome();
                }
            }
        };

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strEmail = email.getText().toString().trim();
                String strPass = password.getText().toString().trim();

                if (strEmail.isEmpty()) {
                    email.setError(getString(R.string.empty_email));
                    email.requestFocus();
                } else if (strPass.isEmpty()){
                    password.setError(getString(R.string.empty_pwd));
                    password.requestFocus();
                } else if (checkEmail(email.getText().toString().trim()) && checkPwd(password.getText().toString().trim())){
                    Log.d(TAG, "login(strEmail, strPass)");
                     login(strEmail, strPass);
                }
            }
        });

        btloginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.login_w_FB, Toast.LENGTH_LONG).show();
            }
        });

        String vFB = FacebookSdk.getSdkVersion();
        System.out.println("Facebook version:".concat(vFB).toString());

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup ();
            }
         });

        btshowPwd.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            email.setText(user.getEmail().toString().trim());
            password.setText(user.getUid().toString().trim());
        } else {
            email.setText(R.string.email);
            password.setText(null);
        }
   }

    public void login (String email, String pwd) {
        firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //finish();
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmailAndPwd:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                    startActivity(new Intent(LoginActivity.this, MenstrualCalendarActivity.class));
                    Toast.makeText(LoginActivity.this, R.string.welcome, Toast.LENGTH_LONG).show();

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // thrown if there already exists an account with the given email address
                        Toast.makeText(LoginActivity.this, R.string.alredy_exist_account, Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // thrown if the email address is malformed
                        Toast.makeText(LoginActivity.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        // thrown if the password is not strong enough
                        Toast.makeText(LoginActivity.this, R.string.pwd_not_strong_enough, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.error_session, Toast.LENGTH_LONG).show();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                System.out.println("onCanceled: batch canceled.");
                Toast.makeText(LoginActivity.this, R.string.cancel_process, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode  == SING_IN_CODE) {
           GoogleSignInResult googleSignInResult =  Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(googleSignInResult);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            firebaseAuthWithGoogle(googleSignInResult.getSignInAccount());
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_session, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        //progressBar.setVisibility(View.VISIBLE);
        btloginGoogle.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //progressBar.setVisibility(View.GONE);
                btloginGoogle.setVisibility(View.VISIBLE);

                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.error_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goHome() {
        Intent intent =  new Intent(this, MenstrualCalendarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void signup () {
        Intent intent =  new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private boolean checkEmail(String stremail) {
        boolean result = true;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(stremail).matches()) {
            email.setError(getString(R.string.invalid_email));
            email.requestFocus();
            result = false;
        }
        return result;
    }

    private boolean checkPwd(String pwd) {
        boolean result = true;

        if (!pwd.matches(".*[!@#$%^&*+=?-].*")){
            password.setError(getString(R.string.pwd_caracter_esp));
            password.requestFocus();
            result = false;
        }

        if (!pwd.matches(".*\\d.*")){
            password.setError(getString(R.string.pwd_number_int));
            password.requestFocus();
            result = false;
        }

        if (!pwd.matches(".*[a-z].*")){
            password.setError(getString(R.string.pwd_lower_case));
            password.requestFocus();
            result = false;
        }

        if (!pwd.matches(".*[A-Z].*")){
            password.setError(getString(R.string.pwd_upper_case));
            password.requestFocus();
            result = false;
        }

        if (!pwd.matches(".{8,15}")){
            password.setError(getString(R.string.pwd_length));
            password.requestFocus();
            result = false;
        }

        if (pwd.matches(".*\\s.*")){
            password.setError(getString(R.string.pwd_no_spaces));
            password.requestFocus();
            result = false;
        }
        return result;
    }

}
