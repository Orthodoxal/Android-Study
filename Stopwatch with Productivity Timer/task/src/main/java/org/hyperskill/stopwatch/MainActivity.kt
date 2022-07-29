package org.hyperskill.stopwatch

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlin.random.Random

const val CHANNEL_ID = "org.hyperskill"

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var secondsLimit = Int.MAX_VALUE
    private var amountSeconds = 0

    private val updateTimer: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("SetTextI18n")
        override fun run() {
            amountSeconds++
            val minutes = (amountSeconds / 60).toString().padStart(2, '0')
            val seconds = (amountSeconds % 60).toString().padStart(2, '0')
            textView.text = "$minutes:$seconds"
            if (amountSeconds == secondsLimit + 1) {
                textView.setTextColor(Color.RED)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Stopwatch"
                    val descriptionText = "Time information"
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                    }
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                val notificationBuilder = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Notification")
                    .setContentText("Time exceeded")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                val notificationManager = this@MainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(393939, notificationBuilder.build())
            }
            val random = Random.Default
            val color =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
            handler.postDelayed(this, 1000)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun reset() {
        handler.removeCallbacks(updateTimer)
        amountSeconds = 0
        textView.text = this.resources.getString(R.string.startTimer)
        textView.setTextColor(R.color.colorPrimary)
        progressBar.visibility = View.INVISIBLE
        settingsButton.isEnabled = true
    }

    private fun start() {
        if (amountSeconds == 0) {
            progressBar.visibility = View.VISIBLE
            handler.postDelayed(updateTimer, 1000)
            settingsButton.isEnabled = false
        }
    }

    private fun setTimerSettings() {
        val contentView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null, false)
        AlertDialog.Builder(this)
            .setTitle("Set upper limit in seconds")
            .setView(contentView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                try {
                    val seconds = contentView.upperLimitEditText.text.toString().toInt()
                    if (seconds > 0) {
                        secondsLimit = seconds
                        Toast.makeText(this, "New seconds limit: $seconds", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (_: Exception) {
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resetButton.setOnClickListener { reset() }
        startButton.setOnClickListener { start() }
        settingsButton.setOnClickListener { setTimerSettings() }
    }
}