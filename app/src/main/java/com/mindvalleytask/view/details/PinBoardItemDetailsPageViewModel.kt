package com.mindvalleytask.view.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.m.downloaderlibrary.model.DownloadFileState

class PinBoardItemDetailsPageViewModel(private val imageDownloader: ImageDownloader) : ViewModel() {

    fun getDownloadImageState(): MutableLiveData<DownloadFileState> {
        imageDownloader.startDownloading()
        return imageDownloader.getDownloadedDateState()
    }
}
