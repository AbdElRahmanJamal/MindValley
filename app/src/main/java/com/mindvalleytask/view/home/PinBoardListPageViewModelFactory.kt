package com.mindvalleytask.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindvalleytask.data.DownloadJsonFile

class PinBoardListPageViewModelFactory(
    private val downloadJsonFile: DownloadJsonFile
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PinBoardListPageViewModel(downloadJsonFile) as T
    }
}