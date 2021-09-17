package com.rosberry.pine.extension

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.rosberry.pine.BuildConfig
import java.io.File

@Suppress("DEPRECATION")
fun Fragment.getScreenWidth(): Int {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowMetrics = requireActivity().windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}

fun Fragment.share(address: String, context: Context?) {
    val file = File(address).toUri()
    val sharedUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID,
            file.toFile())

    val sharingIntent = Intent(Intent.ACTION_SEND)
    context?.grantUriPermission(context?.packageName, sharedUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION)
    sharingIntent.type = "image/*"
    sharingIntent.putExtra(Intent.EXTRA_STREAM, sharedUri)
    startActivity(sharingIntent)
}