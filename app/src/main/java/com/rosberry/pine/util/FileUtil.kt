package com.rosberry.pine.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    suspend fun writeBitmap(directory: File, fileName: String, bitmap: Bitmap): String {
        val file = File(directory, "$fileName.jpeg")

        if (file.exists()) {
            return file.absolutePath
        }

        file.createNewFile()
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }

    suspend fun readBitmap(address: String): Bitmap? {
        val file = File(address)

        if (!file.exists()) {
            return null
        }

        return BitmapFactory.decodeFile(address)
    }
}