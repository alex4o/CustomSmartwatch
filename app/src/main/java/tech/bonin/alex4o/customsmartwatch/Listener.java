package tech.bonin.alex4o.customsmartwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by alex4o on 1/22/18.
 */

public class Listener extends NotificationListenerService {
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        // Implement what you want here
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
    }

    @Override
    public void onCreate() {
        System.out.println("Created!!!");
        System.out.println(super.getActiveNotifications());
    }
}
