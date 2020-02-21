package com.mindvalleytask.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.mindvalleytask.data.DownloadJsonFile

class PinBoardListPageViewModel(private val downloadJsonFile: DownloadJsonFile) : ViewModel() {

    fun getDownloadedJson() = downloadJsonFile.getDownloadedJson()

    fun clearCash() =
        liveData {
            emit(CashingManager.getInstance(MemoryCashingFactory).clearCashedData())
        }


}

