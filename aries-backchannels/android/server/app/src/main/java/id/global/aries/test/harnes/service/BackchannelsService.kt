package id.global.aries.test.harnes.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import id.global.aries.test.harnes.R
import id.global.aries.test.harnes.activity.MainActivity
import io.javalin.Javalin

class BackchannelsService : Service() {

    override fun onCreate() {
        super.onCreate()
        foreground()

        // https://github.com/hyperledger/aries-agent-test-harness/blob/master/openapi-spec.yml
        val app = Javalin.create().start(9020)
        app.post("/agent/command/:topic/:operation") {

        }
        app.get("/agent/command/:topic") { ctx ->
            when (ctx.pathParam("topic")) {
                "status" -> {
                    ctx.html("Status: Thumbs up")
                }
                else -> ctx.status(501)
            }
        }

        app.get("/agent/command/:topic/:id") {

        }

        app.error(501, "html") { ctx ->
            // TODO Dump data from backchannel_operations.csv to help guid implementation
            ctx.html("Not Implemented")
        }

//        app.get("/ping") { ctx -> ctx.result("pong") }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun foreground() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Aries Test harness server")
            .setContentText("Aries Test harness server")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("Aries Test harness server")
            .build()

// Notification ID cannot be 0.
        startForeground(1, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}
