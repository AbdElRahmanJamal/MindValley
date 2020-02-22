package com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.helper.DATA_NOT_FOUND_IN_CASH
import com.m.downloaderlibrary.model.DownloadFileState

class LocalReaderFromMemoryCash {

    var localData = MutableLiveData<DownloadFileState>()

    fun readDataFromCash(cashingManager: CashingManager, casedDataURL: String) {

        localData.postValue(DownloadFileState.LoadingState)

        if (cashingManager.getDownloadedDataFromCash(casedDataURL) != null &&
                cashingManager.getDownloadedDataFromCash(casedDataURL) != "") {

            localData.postValue(DownloadFileState.SuccessState(cashingManager.getDownloadedDataFromCash(casedDataURL)!!))

        } else {
            localData.postValue(DownloadFileState.ErrorState(DATA_NOT_FOUND_IN_CASH))
        }
    }

}