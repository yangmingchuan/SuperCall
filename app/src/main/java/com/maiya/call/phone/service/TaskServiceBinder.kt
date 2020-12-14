package com.maiya.call.phone.service

import android.os.Binder
import java.lang.ref.WeakReference

/**
 * Service 管理
 */
class TaskServiceBinder : Binder() {
    private var weakService: WeakReference<CallListenerService>? = null

    /**
     * Inject service instance to weak reference.
     */
    fun onBind(service: CallListenerService) {
        weakService = WeakReference(service)
    }

    val service: CallListenerService?
        get() = if (weakService == null) null else weakService!!.get()
}