package com.maiya.call.phone.manager

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.maiya.call.App.Companion.context
import com.maiya.call.R
import com.maiya.call.permission.Rom
import com.maiya.call.phone.receiver.NotificationBroadcast
import com.maiya.call.phone.utils.NotificationUtil

/**
 * 自定义通知
 */
class CustomNotifyManager private constructor() {

    companion object {
        private val BLOCK_PHONES = arrayOf("vivo Y31A", "vivo Y51", "vivo Y31"
                , "vivo Y51e", "vivo Y51A", "vivo Y51t L", "vivo Y51n") //部分vivo手机不支持自定义样式
        private const val NUBIA_Z11 = "NX549J" //单独屏蔽这个手机
        private const val notifyName = "消息推送"
        private const val notifyDescription = "通知栏"
        const val STEP_COUNT_NOTIFY_ID = 100
        const val ACTION_NOTIFICATION_CLICK = "com.ACTION_NOTIFICATION_CLICK"
        private const val CHANNEL_ID = "channel_megatron_1"
        private const val CHANNEL_NAME = "SuperCall"
        private const val CHANNEL_CONTENT = "SuperCall时刻在你身边！"
        private var manager: NotificationManager? = null
        private val isMIUI: Boolean = Rom.isMiui()
        @get:Synchronized
        var instance: CustomNotifyManager? = null
            get() {
                if (field == null) {
                    field = CustomNotifyManager()
                }
                return field
            }
            private set

        @Synchronized
        private fun getNotificationManager(context: Context, isDefault: Boolean): NotificationManager? {
            if (manager == null) {
                manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val mChannel: NotificationChannel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var importance = NotificationManager.IMPORTANCE_DEFAULT
                    if (!isDefault) {
                        importance = NotificationManager.IMPORTANCE_LOW
                    }
                    mChannel = NotificationChannel(CHANNEL_ID, notifyName, importance)
                    mChannel.description = notifyDescription
                    if (isDefault) {
                        mChannel.enableLights(true)
                        mChannel.lightColor = Color.RED
                    } else {
                        mChannel.enableLights(false)
                        mChannel.enableVibration(false)
                        mChannel.vibrationPattern = longArrayOf(0)
                        mChannel.setSound(null, null)
                    }
                    val group = NotificationChannelGroup("stick", "通知")
                    manager!!.createNotificationChannelGroup(group)
                    mChannel.group = "stick"
                    manager!!.createNotificationChannel(mChannel)
                }
            }
            return manager
        }

        /**
         * 屏蔽某个手机,走最普通推送
         */
        private fun notShieldPhone(): Boolean {
            val device = Build.MODEL
            if (NUBIA_Z11.contains(device)) {
                return false
            }
            if ("5.1.1" == Build.VERSION.RELEASE) { //vivo 5.1.1部分手机不支持自定义样式
                for (blockPhone in BLOCK_PHONES) {
                    if (blockPhone == device) {
                        return false
                    }
                }
            }
            return true
        }
    }

    fun getNotifyNotification(context: Context): Notification? {
        val builder = getBuilder(context)
        try {
            getNotificationManager(context, false)
            var remoteViews: RemoteViews? = null
            if (notShieldPhone()) {
                remoteViews = if (isMIUI) {
                    RemoteViews(context.packageName,
                            R.layout.layout_item_notification_mi)
                } else {
                    RemoteViews(context.packageName,
                            R.layout.layout_item_notification)
                }
                builder.setCustomContentView(remoteViews)
            }
            val notification = builder
                    .setContentTitle(CHANNEL_NAME)
                    .setContentText(CHANNEL_CONTENT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(null)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setLights(0, 0, 0)
                    .setVibrate(longArrayOf(0))
                    .setSound(null)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .build()
            remoteViews?.let { setNotificationItem(context, notification, it) }
            return notification
        } catch (e: Exception) {
            try {
                return getDefaultNotification(builder)
            } catch (E: Exception) {
            }
        }
        return null
    }

    private fun setNotificationItem(context: Context?, notification: Notification
                                    , remoteViews: RemoteViews?) {
        if (context == null || remoteViews == null) {
            return
        }
        val title = "SuperCall"
        val content = "守护你的来电进程..."
        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_content, content)
    }

    fun getDefaultNotification(builder: NotificationCompat.Builder): Notification {
        return builder
                .setContentTitle(CHANNEL_NAME)
                .setContentText(CHANNEL_CONTENT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(0, 0, 0)
                .setVibrate(longArrayOf(0))
                .setSound(null)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .build()
    }

    private val pendingIntent: PendingIntent
        private get() {
            val context = context
            val intent = Intent(context, NotificationBroadcast::class.java)
            intent.action = ACTION_NOTIFICATION_CLICK
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    private fun getPendingIntent(action: String): PendingIntent {
        val context = context
        val intent = Intent(context, NotificationBroadcast::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getBuilder(context: Context): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && NotificationUtil.findNotificationBuilder()) {
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else NotificationCompat.Builder(context)
    }

}