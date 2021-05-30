package ati.mobil.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "customer_notification_channel";
    private final int NOTIFICATION_ID = 0;

    private NotificationManager m_NotifyManager;
    private Context m_context;

    public NotificationHandler(Context m_Context) {
        this.m_context = m_Context;
        m_NotifyManager = (NotificationManager) m_context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "Customer Notification", NotificationManager.IMPORTANCE_HIGH);

        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setDescription("Notifications from Customer application");

        m_NotifyManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(m_context, CustomerListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(m_context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(m_context, CHANNEL_ID)
                .setContentTitle("Customer Application")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_work)
                .setContentIntent(pendingIntent);

        m_NotifyManager.notify(NOTIFICATION_ID, builder.build());
    }

}
