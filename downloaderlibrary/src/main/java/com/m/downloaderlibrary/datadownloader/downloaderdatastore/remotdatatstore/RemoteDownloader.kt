package com.m.downloaderlibrary.datadownloader.downloaderdatastore.remotdatatstore

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.datadownloader.DataDownloadedFormatter
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class RemoteDownloader(
        private val dataDownloadedFormatter: DataDownloadedFormatter,
        private val downloadDownloadDataType: DownloadDataType,
        private val cashingManager: CashingManager,
        private val IODispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val url by lazy { dataDownloadedFormatter.baseFileDownloader.url }

    var remoteData = MutableLiveData<DownloadFileState>()

    @ExperimentalCoroutinesApi
    private suspend fun getFormattedData(): Flow<DownloadFileState> {
        dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(downloadDownloadDataType)
        return dataDownloadedFormatter.formatterDataFlow
    }

    @ExperimentalCoroutinesApi
    fun startDownloading() {
        CoroutineScope(IODispatcher).launch {
            downloadDataFromServer()
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun downloadDataFromServer() {
        getFormattedData().collect {
            when (it) {

                is DownloadFileState.LoadingState, is DownloadFileState.ErrorState ->
                    remoteData.postValue(it)

                is DownloadFileState.SuccessState -> {
                    cashingManager.putDownloadedDataIntoCash(url, it.downloadedData)
                    remoteData.postValue(DownloadFileState.SuccessState(it.downloadedData))
                }
            }
        }
    }

}