package com.udacity


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadIDsWithFileNames = mutableMapOf<Long, String>()
    private var selectedUrl = ""
    private var selectedFileName = ""

    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> {
                    selectedUrl = GLIDE_URL
                    selectedFileName = getString(R.string.glide_radio_text)
                }
                R.id.radioButton2 -> {
                    selectedUrl = RETROFIT_URL
                    selectedFileName = getString(R.string.retrofit_radio_text)
                }
                else -> {
                    selectedUrl = LOAD_APP_URL
                    selectedFileName = getString(R.string.load_app_radio_text)
                }
            }
        }

        custom_button.setOnClickListener {
            if (selectedUrl.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_file_to_download), Toast.LENGTH_SHORT).show()
            } else {
                download()
                custom_button.buttonState = ButtonState.Loading
            }
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadIDsWithFileNames.contains(id)) {
                custom_button.buttonState = ButtonState.Completed

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                var status = -1
                val shortFileName = downloadIDsWithFileNames[id]?.split(" ")?.first()

                if (cursor.moveToFirst()) {
                    status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                }

                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> notificationManager.sendNotification(
                        id.toInt(),
                        getString(
                            R.string.notification_success_format,
                            shortFileName
                        ),
                        context,
                        downloadIDsWithFileNames[id],
                        getString(R.string.success)
                    )
                    else -> notificationManager.sendNotification(
                        id.toInt(),
                        getString(
                            R.string.notification_failed_format,
                            shortFileName
                        ),
                        context,
                        downloadIDsWithFileNames[id],
                        getString(R.string.failed)
                    )
                }
                downloadIDsWithFileNames.remove(id)
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(selectedFileName)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadIDsWithFileNames[downloadManager.enqueue(request)] =
            selectedFileName// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            
            notificationChannel.description = getString(R.string.download_channel_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }

}
