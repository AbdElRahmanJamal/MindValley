package com.mindvalleytask.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m.downloaderlibrary.downloadertypes.TextDownloader


class PinBoardListPageViewModelFactory(
        private val textDownloader: TextDownloader
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PinBoardListPageViewModel(textDownloader) as T
    }
}