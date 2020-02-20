package com.m.downloaderlibrary.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory


class HelperMethods {

    companion object {

        fun isUrlValid(url: String?): Boolean {
            return !(url == null || url.isEmpty())
        }

        fun createBitmapFromByteArray(data: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }
    }
}