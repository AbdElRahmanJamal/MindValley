package com.m.downloaderlibrary.datadownloader

import android.graphics.Bitmap
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.helper.HelperMethods
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class DataDownloadedFormatter(private val baseFileDownloader: BaseFileDownloader) {

    lateinit var formatterDataFlow: Flow<DownloadFileState>

    @ExperimentalCoroutinesApi
    suspend fun formatDownloadedDataBaseOnDownloadDataType(downloadDataType: DownloadDataType) {

        formatterDataFlow = flow {

            baseFileDownloader.downloadFromURL()
            baseFileDownloader.getDownloadedData().collect {
                when (it) {
                    is DownloadFileState.LoadingState, is DownloadFileState.ErrorState -> emit(it)

                    is DownloadFileState.SuccessState ->

                        emit(DownloadFileState.SuccessState(formatData(it.downloadedData, downloadDataType)))
                }
            }
        }.distinctUntilChanged()
    }

    private fun formatData(downloadFileResult: Any, downloadDataType: DownloadDataType): Any {

        val formattedByteArrayOutputStream = downloadFileResult as ByteArrayOutputStream

        return when (downloadDataType) {
            DownloadDataType.JSON -> String(formattedByteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8)

            DownloadDataType.IMAGE -> getBitmapFromDownloadedData(formattedByteArrayOutputStream.toByteArray())
        }
    }

    private fun getBitmapFromDownloadedData(it: ByteArray): Bitmap {
        return HelperMethods.createBitmapFromByteArray(it)
    }

}