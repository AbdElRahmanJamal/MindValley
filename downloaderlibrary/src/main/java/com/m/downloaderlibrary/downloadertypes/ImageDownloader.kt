package com.m.downloaderlibrary.downloadertypes

import android.graphics.Bitmap
import android.widget.ImageView
import com.m.downloaderlibrary.datadownloader.downloaderrepository.DownloaderRepository
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ImageDownloader(private val downloaderRepository: DownloaderRepository,
                      private val placeHolderImage: Int = 0,
                      private val errorImage: Int = 0,
                      private val imageView: ImageView

) {

    @ExperimentalCoroutinesApi
    fun downloadImage() {
        downloaderRepository.getDownloadedData().observeForever {
            when (it) {
                is DownloadFileState.LoadingState -> onLoading()
                is DownloadFileState.SuccessState -> onSuccess(it.downloadedData as Bitmap)
                is DownloadFileState.ErrorState -> onError()
            }
        }
    }

    private fun onLoading() {
        imageView.apply {
            setBackgroundResource(placeHolderImage)
        }
    }

    private fun onSuccess(bitmap: Bitmap) {
        imageView.apply {
            setImageBitmap(bitmap)
        }
    }

    private fun onError() {
        imageView.apply {
            setBackgroundResource(errorImage)
        }

    }
}