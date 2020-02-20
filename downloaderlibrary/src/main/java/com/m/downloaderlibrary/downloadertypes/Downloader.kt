package com.m.downloaderlibrary.downloadertypes

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.flow.Flow

abstract class Downloader(
    private val dataDownloadedFormatter: DataDownloadedFormatter,
    private val downloadDownloadDataType: DownloadDataType
) {

    val downloadedURL by lazy { dataDownloadedFormatter.baseFileDownloader.url }


    protected var getDownloadObject = MutableLiveData<DownloadFileState>()
    protected suspend fun getDownloadedData(): Flow<DownloadFileState> {
        dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(downloadDownloadDataType)
        return dataDownloadedFormatter.formatterDataFlow
    }

    abstract fun startDownloading()
    abstract suspend fun readDataFromCash()
    abstract suspend fun downloadDataFromServer()
    fun getDownloadedDateState() = getDownloadObject

}