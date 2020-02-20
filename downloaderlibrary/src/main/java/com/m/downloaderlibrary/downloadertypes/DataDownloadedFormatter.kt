package com.m.downloaderlibrary.downloadertypes

import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class DataDownloadedFormatter(
    val baseFileDownloader: BaseFileDownloader
) {

    lateinit var formatterDataFlow: Flow<DownloadFileState>

    suspend fun formatDownloadedDataBaseOnDownloadDataType(downloadDataType: DownloadDataType) {

        formatterDataFlow = flow {
            baseFileDownloader.downloadFromURL()
            baseFileDownloader.getDownloadedData().collect {
                when (it) {
                    is DownloadFileState.LoadingState, is DownloadFileState.ErrorState -> emit(it)
                    is DownloadFileState.SuccessState -> emit(
                        DownloadFileState.SuccessState(
                            formatData(it.downloadFileResult, downloadDataType)
                        )
                    )
                }
            }
        }
    }

    private fun formatData(
        downloadFileResult: ContentURLResult,
        downloadDataType: DownloadDataType
    ): ContentURLResult {

        val formattedByteArrayOutputStream =
            downloadFileResult.downloadedData as ByteArrayOutputStream
        return when (downloadDataType) {
            DownloadDataType.JSON -> ContentURLResult(
                String(
                    formattedByteArrayOutputStream.toByteArray(),
                    StandardCharsets.UTF_8
                )
            )
            DownloadDataType.IMAGE -> ContentURLResult(formattedByteArrayOutputStream.toByteArray())
        }
    }
}