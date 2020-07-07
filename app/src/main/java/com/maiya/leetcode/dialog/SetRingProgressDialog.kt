package com.maiya.leetcode.dialog

import android.content.Context
import com.maiya.leetcode.R
import com.maiya.leetcode.widget.ArcProgressBar

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