package com.m.downloaderlibrary.downloadertypes

import com.m.downloaderlibrary.helper.CANCEL_DOWNLOAD
import com.m.downloaderlibrary.helper.EMPTY_RESPONSE
import com.m.downloaderlibrary.helper.HelperMethods.Companion.isUrlValid
import com.m.downloaderlibrary.helper.INVALID_URL
import com.m.downloaderlibrary.helper.OPEN_CONNECTION_ERROR
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL

class BaseFileDownloader(var url: String) {

    private lateinit var dataFlow: Flow<DownloadFileState>
    private var isDownloadCanceled = false

    suspend fun downloadFromURL() {

        val byteArrayOutputStream = ByteArrayOutputStream()

        dataFlow = flow {
            emit(DownloadFileState.LoadingState)

            if (isUrlValid(url)) {

                runCatching {
                    var count = 0
                    val connectionURL = URL(url)
                    val connection = connectionURL.openConnection()
                    connection.connect()
                    val input = BufferedInputStream(connectionURL.openStream(), 8192)
                    val data = ByteArray(1024)

                    while (!isDownloadCanceled && count != -1) {
                        byteArrayOutputStream.write(data, 0, count)
                        count = input.read(data)

                        if (isDownloadCanceled)
                            emit(DownloadFileState.ErrorState(CANCEL_DOWNLOAD))
                    }
                    input.close()
                }.onSuccess {
                    if (!isDownloadCanceled && byteArrayOutputStream.size() > 0) {
                        emit(DownloadFileState.SuccessState(ContentURLResult(byteArrayOutputStream)))
                    } else {
                        emit(DownloadFileState.ErrorState(EMPTY_RESPONSE))
                    }
                }.onFailure {
                    emit(DownloadFileState.ErrorState(OPEN_CONNECTION_ERROR))
                }
            } else {
                emit(DownloadFileState.ErrorState(INVALID_URL))
            }
        }
    }

    private fun cancelDownload() {
        isDownloadCanceled = true
    }

    public fun isDownloadCanceled() = isDownloadCanceled
    public fun getDownloadedData() = dataFlow

}