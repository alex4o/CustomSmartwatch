package tech.bonin.alex4o.customsmartwatch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static tech.bonin.alex4o.customsmartwatch.ConnectedKt.send;

/**
 * Created by alex4o on 1/23/18.
 */

public class NListener extends NotificationListenerService {
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        // Implement what you want here
        System.out.println("shit shit\n\n\n\n\n\nshit");
        send(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
        System.out.println("removed");

    }

    @Override
    public void onListenerConnected() {
        System.out.println("Created!!!");
        System.out.println(super.getActiveNotifications());
        for(StatusBarNotification sb : super.getActiveNotifications()){
            send(sb);
        }
    }

}
