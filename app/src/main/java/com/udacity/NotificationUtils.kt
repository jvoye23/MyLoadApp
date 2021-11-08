package com.udacity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
const val EXTRA_NOTIFICATION_ID = "com.udacity.NOTIFICATION_ID"
const val EXTRA_FILE_NAME = "com.udacity.FILE_NAME"
const val EXTRA_STATUS = "com.udacity.STATUS"

fun NotificationManager.sendNotification(notificationID: Int, messageBody: String, applicationContext: Context, fileName: String?, status: String) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(EXTRA_NOTIFICATION_ID, notificationID)
        putExtra(EXTRA_FILE_NAME, fileName)
        putExtra(EXTRA_STATUS, status)
    }

    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_action),
            detailPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setDefaults(Notification.DEFAULT_ALL)

    notify(notificationID, builder.build())
}