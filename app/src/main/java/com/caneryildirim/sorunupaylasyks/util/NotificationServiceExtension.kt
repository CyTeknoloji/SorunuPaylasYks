package com.caneryildirim.sorunupaylasyks.util

import android.content.Context
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal

class NotificationServiceExtension: OneSignal.OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(p0: Context?, p1: OSNotificationReceivedEvent?) {
        if (p1!=null){
            val notification=p1.notification
            val mutableNotification=notification.mutableCopy()
            p1.complete(mutableNotification)
        }

    }
}