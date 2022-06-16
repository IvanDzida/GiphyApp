package com.yolo.giphyapp.ui.custom

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.yolo.giphyapp.R
import kotlinx.android.synthetic.main.upload_progress.*
import kotlin.Int

class UploadProgress(private val activity: Activity) {
    private lateinit var dialog: AlertDialog

    fun startLoading() {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.upload_progress, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("StringFormatInvalid")
    fun updateProgress(currentProgress: Int) {
        dialog.progress_bar.progress = currentProgress

        dialog.progress_text.text =
            String.format(
                dialog.context.resources.getString(R.string.progress_percentage),
                "$currentProgress"
            )
    }

    fun dismiss() {
        dialog.dismiss()
    }
}