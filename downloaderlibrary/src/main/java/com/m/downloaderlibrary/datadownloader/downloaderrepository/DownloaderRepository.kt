package com.m.downloaderlibrary.datadownloader.downloaderrepository

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore.LocalReaderFromMemoryCash
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.remotdatatstore.RemoteDownloader
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi

class DownloaderRepository(private val cashingManager: CashingManager,
                           private val remoteDownloader: RemoteDownloader,
                           private val localReaderFromMemoryCash: LocalReaderFromMemoryCash,
                           private val cashedURL: String

) {

    @ExperimentalCoroutinesApi
    fun getDownloadedData(): MutableLiveData<DownloadFileState> {

        return if (cashingManager.isDownloadedDataFoundInCashingManager(cashedURL)) {
            localReaderFromMemoryCash.readDataFromCash(cashingManager, cashedURL)
            localReaderFromMemoryCash.localData
        } else {
            remoteDownloader.startDownloading(cashingManager, cashedURL)
            remoteDownloader.remoteData
        }

    }
}