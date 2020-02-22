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
        private val IODispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    var remoteData = MutableLiveData<DownloadFileState>()

    @ExperimentalCoroutinesApi
    private suspend fun getFormattedData(): Flow<DownloadFileState> {
        dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(downloadDownloadDataType)
        return dataDownloadedFormatter.formatterDataFlow
    }

    @ExperimentalCoroutinesApi
    fun startDownloading(cashingManager: CashingManager, casedDataURL: String) {
        CoroutineScope(IODispatcher).launch {
            downloadDataFromServer(cashingManager, casedDataURL)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun downloadDataFromServer(cashingManager: CashingManager, casedDataURL: String) {
        getFormattedData().collect {
            when (it) {

                is DownloadFileState.LoadingState, is DownloadFileState.ErrorState ->
                    remoteData.postValue(it)

                is DownloadFileState.SuccessState -> {
                    cashingManager.putDownloadedDataIntoCash(casedDataURL, it.downloadedData)
                    remoteData.postValue(DownloadFileState.SuccessState(it.downloadedData))
                }
            }
        }
    }

}