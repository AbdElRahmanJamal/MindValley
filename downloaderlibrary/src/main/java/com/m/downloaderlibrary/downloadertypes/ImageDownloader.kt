package com.m.downloaderlibrary.downloadertypes

import android.graphics.Bitmap
import android.widget.ImageView
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.helper.HelperMethods.Companion.createBitmapFromByteArray
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDownloader(
    private val dataDownloadedFormatter: DataDownloadedFormatter,
    private val IODispatcher: CoroutineDispatcher = IO,
    private val MainDispatcher: CoroutineDispatcher = Main,
    private var placeholder: Int = 0,
    private var drawableOnError: Int = 0,
    private var mImageView: ImageView? = null,
    private val cashingManager: CashingManager = CashingManager.getInstance(MemoryCashingFactory)
) : Downloader(
    dataDownloadedFormatter,
    DownloadDataType.IMAGE
) {

    private lateinit var mBitmap: Bitmap

    override fun startDownloading() {
        CoroutineScope(IODispatcher).launch {
            if (cashingManager.isDownloadedDataFoundInCashingManager(downloadedURL)) {
                readDataFromCash()
            } else {
                downloadDataFromServer()
            }
        }
    }

    override suspend fun readDataFromCash() {
        val downloadFileResult =
            ContentURLResult(cashingManager.getDownloadedDataFromCash(downloadedURL))
        onDataFetchedSuccessfully(downloadFileResult)
        getDownloadObject.postValue(DownloadFileState.SuccessState(downloadFileResult))
    }

    override suspend fun downloadDataFromServer() {
        getDownloadedData().collect {
            when (it) {
                is DownloadFileState.LoadingState -> onLoading()
                is DownloadFileState.SuccessState -> onDataFetchedSuccessfully(it.downloadFileResult)
                is DownloadFileState.ErrorState -> onError()
            }
            getDownloadObject.postValue(it)
        }
    }

    private suspend fun onLoading() {
        mImageView?.let {
            withContext(MainDispatcher) {
                it.setBackgroundResource(placeholder)
            }
        }
    }

    private suspend fun onDataFetchedSuccessfully(downloadFileResult: ContentURLResult) {
        val image = downloadFileResult.downloadedData as ByteArray
        mBitmap = createBitmapFromByteArray(image)!!
        cashingManager.putDownloadedDataIntoCash(downloadedURL, image)

        mImageView?.let {
            withContext(MainDispatcher) {
                it.setImageBitmap(mBitmap)
            }
        }
    }

    private suspend fun onError() {
        mImageView?.let {
            withContext(MainDispatcher) {
                it.setBackgroundResource(drawableOnError)
            }
        }
    }
}