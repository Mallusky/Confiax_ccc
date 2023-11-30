package com.ccc.confiax;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AfterBootReceiver extends BroadcastReceiver {

    MCRecalculateReceiver MCRecalculateReceiver = new MCRecalculateReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            MCRecalculateReceiver.setPredictionService(context);
        }
    }
}
