package com.schaffer.base.kotlin.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import java.io.File
import java.net.URI
import java.net.URISyntaxException

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date : 2018/2/1
 * Project : SchafferBaseLibrary
 * Package : com.schaffer.base.receiver
 * Description :
 */

class DownloadReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val download_id = intent.getLongExtra("download_id", -1)
                val mimeType = intent.getStringExtra("download_mimeType")
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val file_uri = dm.getUriForDownloadedFile(download_id)
                if (file_uri != null) {
                    try {
                        val file = File(URI(file_uri.toString()))
                        val install = Intent(Intent.ACTION_VIEW)
                        if (!TextUtils.isEmpty(mimeType)) {
                            install.setDataAndType(file_uri, mimeType)
                        }
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(install)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                        return
                    }

                }
            }
        }
    }
}
