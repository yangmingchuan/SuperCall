package com.maiya.call.dialog

import android.content.Context
import com.maiya.call.R
import com.maiya.call.widget.ArcProgressBar

/**
 * 设置视频铃声进度dialog
 */

class SetRingProgressDialog(context: Context) : DelegateDialog(context, R.style.SetRingProgressDialog) {

    private lateinit var progressBar: ArcProgressBar

    init {
        createView()
    }

    private inline fun createView() {
        setContentView(R.layout.dialog_set_ring_progress)
        progressBar = findViewById(R.id.progress)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setDefWindowParams()
    }

    fun updateProgress(max: Int, progress: Int) {
        progressBar.updateProgress(max, progress)
    }
}