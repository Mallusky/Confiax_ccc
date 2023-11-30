package com.ccc.confiax.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.ccc.confiax.MenstrualCalendarActivity;
import com.ccc.confiax.R;
import com.ccc.confiax.managers.MenstrualCycleManager;
import com.ccc.confiax.utils.AppPreferences;

import org.joda.time.LocalDate;

import java.util.Set;


public class MCPredictionService extends IntentService {
    private static final String TAG = "MCPredictionService";

    public static final String ACTION_SCHEDULED_RECALCULATION
            = AppPreferences.APPLICATION_PREFIX + "action.SCHEDULED_RECALCULATION";

    private MenstrualCycleManager mcManager;
    private MenstrualCycleCalculator mcCalculator;

    public MCPredictionService() {
        super("MCPredictionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mcManager = new MenstrualCycleManager(getApplicationContext());
        mcCalculator = new MenstrualCycleCalculator(mcManager);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (action == null) {
                Log.e(TAG, "La acción viene a nulo" + intent.toString());
                return;
            }

            switch (action) {
                case ACTION_SCHEDULED_RECALCULATION:
                    Log.i(TAG, "Recalculando...");
                    mcCalculator.calculate();
                    checkStatusAndSendNotifications();
                    break;
                default:
                    throw new IllegalArgumentException("Error desconocido :\"" + action + "\"");
            }
        }
    }

    private void checkStatusAndSendNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        LocalDate theNextDay = (new LocalDate()).plusDays(1);

        //
        if (mcManager.sendPeriodNotification()) {
            Set<LocalDate> period = mcManager.getHistoricPeriodDays();

            if (period.contains(theNextDay)) {
                sendPeriodNotification(notificationManager);
            }
        }

        if (mcManager.sendFertileNotification()) {
            Set<LocalDate> fertile = mcManager.getHistoricFertileDays();

            if (fertile.contains(theNextDay)) {
                sendFertileNotification(notificationManager);
            }
        }

        if (mcManager.sendOvulationNotification()) {
            Set<LocalDate> ovulation = mcManager.getHistoricOvulationDays();

            if (ovulation.contains(theNextDay)) {
                sendOvulationNotification(notificationManager);
            }
        }
    }

    private void sendOvulationNotification(NotificationManager notificationManager) {
        sendNotification(notificationManager, AppPreferences.OVULATION_NOTIFICATION_ID,
                getString(R.string.ovulation_notification_title), getString(R.string.ovulation_notification_text));
    }

    private void sendFertileNotification(NotificationManager notificationManager) {
        sendNotification(notificationManager, AppPreferences.FERTILE_NOTIFICATION_ID,
                getString(R.string.fertile_notification_title), getString(R.string.fertile_notification_text));
    }

    private void sendPeriodNotification(NotificationManager notificationManager) {
        sendNotification(notificationManager, AppPreferences.PERIOD_NOTIFICATION_ID,
                getString(R.string.period_notification_title), getString(R.string.period_notification_text));
    }

    private void sendNotification(NotificationManager manager, int notificationId,
                                  String contentTitle, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gotafeliz)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            manager.notify(notificationId, builder.build());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Segunda oportunidad");
            alertDialogBuilder
                    .setMessage("Click 'Retry' para obtener permisos\n\n"+"Click 'Exit' para cerrar Confiax")
                    .setCancelable(false)
                    .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Integer.parseInt(WRITE_EXTERNAL_STORAGE));
                            Intent i = new Intent(MCPredictionService.this, MenstrualCalendarActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void finish() {

    }
}