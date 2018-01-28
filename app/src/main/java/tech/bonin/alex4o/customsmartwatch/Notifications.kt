package tech.bonin.alex4o.customsmartwatch

import android.accessibilityservice.AccessibilityService;
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.R.attr.notificationTimeout
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Notification


class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val data = event?.parcelableData

        if(data != null){
            var notification = data as Notification

            println(notification)


        }
    }


    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()

        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.notificationTimeout = 100
        serviceInfo = info

        println("Service Connected")
    }
}
