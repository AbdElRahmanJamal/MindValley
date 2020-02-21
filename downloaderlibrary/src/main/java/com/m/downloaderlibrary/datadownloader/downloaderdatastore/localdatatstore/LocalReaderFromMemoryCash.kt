package com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.helper.DATA_NOT_FOUND_IN_CASH
import com.m.downloaderlibrary.model.DownloadFileState

class LocalReaderFromMemoryCash(private val cashingManager: CashingManager, private val casedDataURL: String) {

    var localData = MutableLiveData<DownloadFileState>()

    fun readDataFromCash() {
        localData.value = DownloadFileState.LoadingState
        if (cashingManager.isDownloadedDataFoundInCashingManager(casedDataURL)) {
            localData.value = DownloadFileState.SuccessState(cashingManager.getDownloadedDataFromCash(casedDataURL))
        } else {
            localData.value = DownloadFileState.ErrorState(DATA_NOT_FOUND_IN_CASH)
        }
    }

}