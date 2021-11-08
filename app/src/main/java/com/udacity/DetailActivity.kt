package com.udacity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationID = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationID != -1) {
            val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.cancel(notificationID)
        }

        file_name.text = intent.getStringExtra(EXTRA_FILE_NAME) ?: ""

        val loadingStatus = intent.getStringExtra(EXTRA_STATUS) ?: ""
        status.text = loadingStatus

        if (loadingStatus == getString(R.string.failed)) {
            status.setTextColor(Color.RED)
        } else {
            status.setTextColor(getColor(R.color.colorPrimaryDark))
        }

        ok_button.setOnClickListener {
            onBackPressed()
        }
    }
}
