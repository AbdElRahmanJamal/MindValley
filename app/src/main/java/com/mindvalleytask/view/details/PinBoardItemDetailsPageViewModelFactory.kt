package com.mindvalleytask.view.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.m.downloaderlibrary.downloadertypes.ImageDownloader

class PinBoardItemDetailsPageViewModelFactory(
    private val imageDownloader: ImageDownloader
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PinBoardItemDetailsPageViewModel(imageDownloader) as T
    }
}