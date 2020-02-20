package com.m.downloaderlibrary.downloadertypes

import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//here support download text or download text and convert it to json
//2 type of download text and json
class TextDownloader(
    private val dataDownloadedFormatter: DataDownloadedFormatter,
    private val IODispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val cashingManager: CashingManager = CashingManager.getInstance(MemoryCashingFactory)
) : Downloader(
    dataDownloadedFormatter,
    DownloadDataType.JSON
) {

    override fun startDownloading() {
        CoroutineScope(IODispatcher).launch {
            if (cashingManager.isDownloadedDataFoundInCashingManager(downloadedURL)) {
                readDataFromCash()
            } else {
                downloadDataFromServer()

            }
        }
    }

    override suspend fun readDataFromCash() {
        getDownloadObject.postValue(
            DownloadFileState.SuccessState(
                ContentURLResult(
                    cashingManager.getDownloadedDataFromCash(
                        downloadedURL
                    )
                )
            )
        )
    }

    override suspend fun downloadDataFromServer() {
        getDownloadedData().collect {
            when (it) {
                is DownloadFileState.LoadingState, is DownloadFileState.ErrorState -> getDownloadObject.postValue(
                    it
                )
                is DownloadFileState.SuccessState -> onDownloadSuccess(it.downloadFileResult)
            }
        }
    }

    private fun onDownloadSuccess(downloadFileResult: ContentURLResult) {
        cashingManager.putDownloadedDataIntoCash(
            downloadedURL,
            downloadFileResult.downloadedData as String
        )
        getDownloadObject.postValue(DownloadFileState.SuccessState(downloadFileResult))
    }
}