package com.mindvalleytask.data

import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.downloadertypes.TextDownloader
import com.m.downloaderlibrary.model.DownloadFileState

const val BASE_API = "https://pastebin.com/raw/wgkJgazE"

class DownloadJsonFile(private val downloadedFormatter: DataDownloadedFormatter) {


    fun getDownloadedJson(): MutableLiveData<DownloadFileState> {
        val textDownloader = TextDownloader(downloadedFormatter)
        textDownloader.startDownloading()
        return textDownloader.getDownloadedDateState()
    }
}