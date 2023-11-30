package com.ccc.confiax;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ccc.confiax.dtos.Id;
import com.ccc.confiax.dtos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText name, email, password;
    private EditText birthday;
    private Button btBirthday;
    private Button submit;
    private Button btshowPwd;

    private FirebaseAuth firebaseAuth;
    private User user;
    private Id id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        birthday = (EditText) findViewById(R.id.bdaycalendar);
        btBirthday = findViewById(R.id.btbirthday);
        submit = (Button) findViewById(R.id.submit);
        btshowPwd = (Button) findViewById(R.id.imgBtShowPwd);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()){
                    name.setError(getString(R.string.empty_name));
                    name.requestFocus();
                } else if( email.getText().toString().isEmpty()){
                    email.setError(getString(R.string.empty_email));
                    email.requestFocus();
                } else if(password.getText().toString().isEmpty()){
                    password.setError(getString(R.string.empty_pwd));
                    password.requestFocus();
                } else if(birthday.getText().toString().isEmpty()){
                    birthday.setError(getString(R.string.empty_birth));
                    birthday.requestFocus();
                } else {
                   if (checkEmail(email.getText().toString().trim()) && checkPwd(password.getText().toString().trim())){
                    createUser(email.getText().toString(), password.getText().toString());
                    Toast.makeText(v.getContext(), getString(R.string.signup_succes), Toast.LENGTH_LONG).show();}
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

        btBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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

    private void showDatePickerDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        birthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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

    public void createUser (String email, String password) {
    firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmailAndPwd:success");
                    if (user != null) {

                        User user2save = new User();
                        user2save.setEmail(email);
                        user2save.setPassword(password);
                        initUser(user2save);

                        // Se graba el nuevo usuario en la Database de Firestore con los mismos datos de autenticación (email y password) más los datos de 'Nombre' y 'Fecha de Nacimiento'
                        boolean res = saveUser(user2save);

                        if (res) {
                            // Una vez registrados con éxito, nos redirige a la Home
                            goHome();
                        } else {
                            Toast.makeText(SignUpActivity.this, R.string.error_session, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, R.string.error_session, Toast.LENGTH_LONG).show();
                        // Si/No
                        updateUI(user);
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // thrown if there already exists an account with the given email address
                        Toast.makeText(SignUpActivity.this, R.string.alredy_exist_account, Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // thrown if the email address is malformed
                        Toast.makeText(SignUpActivity.this, R.string.invalid_email, Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        // thrown if the password is not strong enough
                        Toast.makeText(SignUpActivity.this, R.string.pwd_not_strong_enough, Toast.LENGTH_LONG).show();
                    }
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

    public void goHome() {
        Intent intent =  new Intent(this, MenstrualCalendarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public String getUID () {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return null;
        } else {
            return user.getUid();
        }
    }

    private void initUser(User user){
        user.setName(name.getText().toString().trim());
        user.setBirthday(birthday.getText().toString().trim());
        String uid = getUID();
        Id id= new Id();
        id.setIdUser(uid);
        user.setIdUser(id);
    }

    public boolean isNull (User user){
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getBirthday() == null) {
            return true;
        }
        return false;
    }

    public boolean saveUser (User user)  {
        try {
            if (!Objects.isNull(user) || isNull(user)) {
                HashMap hashmap = new HashMap<>();
                hashmap.put("Id", user.getIdUser().getIdUser());
                hashmap.put("Name", user.getName());
                hashmap.put("Email", user.getEmail());
                hashmap.put("Password", user.getPassword());
                hashmap.put("Birthday", user.getBirthday());

                db.collection("Users").document().set(hashmap);
            }
        }catch (Exception e){
            /*TODO: pendiente gestionar excepciones*/
          e.printStackTrace();
          return false;
        }
      return true;
    }



}




