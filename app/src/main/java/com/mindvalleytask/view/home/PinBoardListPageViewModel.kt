package com.mindvalleytask.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.downloadertypes.TextDownloader
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.Utils
import com.mindvalleytask.model.BaseResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PinBoardListPageViewModel(private val textDownloader: TextDownloader) : ViewModel() {

    @ExperimentalCoroutinesApi
    private val jsonState = MutableLiveData<DownloadFileState>()

    @ExperimentalCoroutinesApi
    fun getDownloadedJson(): MutableLiveData<DownloadFileState> {

        textDownloader.downloadText().observeForever {
            when (it) {
                is DownloadFileState.LoadingState, is DownloadFileState.ErrorState -> jsonState.value = it
                is DownloadFileState.SuccessState ->
                    jsonState.value =
                            DownloadFileState.SuccessState(Utils.convertStringToBaseModel(it.downloadedData as String) as List<BaseResponse>)
            }
        }
        return jsonState
    }

    fun clearCash() =
            liveData {
                emit(CashingManager.getInstance(MemoryCashingFactory).clearCashedData())
            }


}

