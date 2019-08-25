package com.mega.matrimony.Firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String KEY_OTHER_ID = "other_id";
    private static final String KEY_INTEREST_TAG = "interest_tag";
    private static final String KEY_MATRI_ID = "matri_id";
    private static final String KEY_PHOTO_PASSWORD_TAG = "ppassword_tag";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_NOTIFICATION_TYPE = "noti_type";
    private static final String KEY_MESSAGE = "message";

    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        AppDebugLog.print("FCM Token : " + s);
        SessionManager session = new SessionManager(this);
        session.setUserData(SessionManager.KEY_DEVICE_TOKEN, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Log.e(TAG, "From: " +remoteMessage.getData());

        if (remoteMessage == null)
            return;

        AppDebugLog.print("remoteMessage in onMessageReceived : " + remoteMessage);
        handleDataMessage(remoteMessage);

    }

    private void handleDataMessage(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey(KEY_NOTIFICATION_TYPE)) {
            if (data.get(KEY_NOTIFICATION_TYPE).equals(KEY_MESSAGE)) {
                Intent pushNotification1 = new Intent(Utils.OUICK_TAG);
                pushNotification1.putExtra(KEY_MESSAGE, new HashMap<String, String>(data));
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification1);

                Intent pushNotification2 = new Intent(Utils.CHAT_TAG);
                pushNotification2.putExtra(KEY_MESSAGE, new HashMap<String, String>(data));
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification2);
            }
        }

        Log.e(TAG, "From " + data);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

        Intent pushNotification = new Intent(remoteMessage.getNotification().getClickAction());

        if (data.containsKey(KEY_OTHER_ID))
            pushNotification.putExtra(KEY_OTHER_ID, data.get(KEY_OTHER_ID));
        if (data.containsKey(KEY_INTEREST_TAG))
            pushNotification.putExtra(KEY_INTEREST_TAG, data.get(KEY_INTEREST_TAG));
        if (data.containsKey(KEY_MATRI_ID))
            pushNotification.putExtra(KEY_MATRI_ID, data.get(KEY_MATRI_ID));
        if (data.containsKey(KEY_PHOTO_PASSWORD_TAG))
            pushNotification.putExtra(KEY_PHOTO_PASSWORD_TAG, data.get(KEY_PHOTO_PASSWORD_TAG));

        showNotificationMessage(getApplicationContext(), data.get(KEY_TITLE), data.get(KEY_BODY), ts.toString(), pushNotification);

    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        Spanned spannedMsg = Html.fromHtml(message);
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, spannedMsg.toString(), timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
