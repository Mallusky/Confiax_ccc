package com.ccc.confiax;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ccc.confiax.components.CircularSeekBar;
import com.ccc.confiax.managers.MenstrualCycleManager;
import com.ccc.confiax.utils.AppPreferences;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MenstrualCalendarActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CircularSeekBar csb;
    private CircularSeekBar csbsmall;
    private TextView nick;
    //private ImageView photoUser;

    private ImageView photoUser;
    private Button logout;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static Set<LocalDate> periodDays = new HashSet<>();
    private static Set<LocalDate> fertileDays = new HashSet<>();
    private static Set<LocalDate> ovulationDays = new HashSet<>();
    private MenstrualCycleManager mcManager;
    private CustomCalendarView customCalendarView;
    public static final int GATHER_NEW_PREFERENCES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

       // csb = findViewById(R.id.csb);
       // csbsmall = findViewById(R.id.csbsmall);
        //csbsmall.setVisibility (View.INVISIBLE);

        nick = findViewById(R.id.nick);
        photoUser = (ImageView) findViewById(R.id.photouser);
        logout = findViewById(R.id.logout);
        customCalendarView = findViewById(R.id.calendar_view);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,  this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        /*csb.setOnProgressChangeListener(new CircularSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean isUser) {
                csbsmall.setProgress(progress);
                csbsmall.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }
        });*/

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user !=null) {
                    setUserData(user);
                } else {
                    goLogin();
                }
            }
        };

        mcManager = new MenstrualCycleManager(this);
        periodDays = mcManager.getHistoricPeriodDays();
        fertileDays = mcManager.getHistoricFertileDays();
        ovulationDays = mcManager.getHistoricOvulationDays();

        SharedPreferences preferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        if (!preferences.contains(AppPreferences.BASIC_USER_PREFERENCES_AVAILABLE)) {
            startPreferencesActivity();
        }
            refreshCalendar();

        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photolayer);
        RoundedBitmapDrawable roundedBitmapDrawable  = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);
        photoUser.setImageDrawable(roundedBitmapDrawable);*/

    }

    private void setUserData(FirebaseUser user) {
        if (user!= null) {
            nick.setText(user.getDisplayName());
            //photoUser.setImageURI(user.getPhotoUrl());
            //Picasso
            if (user.getPhotoUrl()!= null) {
                        Picasso.get()
                        .load(user.getPhotoUrl().toString())
                        .placeholder(R.drawable.photolayer)
                        .error(R.drawable.photostudio_1610492474016)
                        .into(photoUser);
            }
        }
    }

    private void refreshCalendar() {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        customCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        customCalendarView.setShowOverflowDate(true);

        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new SampleDayDecorator());
        customCalendarView.setDecorators(decorators);
        customCalendarView.refreshCalendar(currentCalendar);
     }

    private class SampleDayDecorator implements DayDecorator {
        @SuppressLint("ResourceAsColor")
        @Override
        public void decorate(DayView dayView) {
            LocalDate actualDate = new LocalDate(dayView.getDate());
            int color;
            dayView.setBackgroundColor(getResources().getColor(R.color.semi_transparent_r));

            if (periodDays.contains(actualDate)) {
                color = Color.parseColor("#D81B60");
                dayView.setBackgroundColor(color);
            }

            if (fertileDays.contains(actualDate)) {
                color = Color.parseColor("#E4ADBB");
                dayView.setBackgroundColor(color);
            }

            if (ovulationDays.contains(actualDate)) {
                color = Color.parseColor("#915F6D");
                dayView.setBackgroundColor(color);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GATHER_NEW_PREFERENCES) {
            periodDays = mcManager.getHistoricPeriodDays();
            fertileDays = mcManager.getHistoricFertileDays();
            ovulationDays = mcManager.getHistoricOvulationDays();
            refreshCalendar();
        }
    }

    private void startPreferencesActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, GATHER_NEW_PREFERENCES);
    }

    public void goToPreferences(View view) {
        startPreferencesActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private void goLogin() {
        Intent intent =  new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logOut(View v) {
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(status -> {
            if (status.isSuccess()) {
                goLogin();

            } else {
                Toast.makeText(v.getContext(), R.string.exception_logout, Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}