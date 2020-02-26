package edu.socialmedia.ridwan.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProductReminderBroadcastReceiver extends BroadcastReceiver {
    public static final String EXTRA_P_NAME = "edu.socialmedia.ridwan.extra.PRODUCT_NAME";
   // public static final String EXTRA_P_ID = "edu.socialmedia.ridwan.extra.PRODUCT_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String productName = intent.getStringExtra(EXTRA_P_NAME);

        ProductReminderNotification.notify(context, productName+ " Have Expired");



    }
}
