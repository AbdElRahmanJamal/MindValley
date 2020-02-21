package com.m.downloaderlibrary.model

sealed class DownloadFileState {

    object LoadingState : DownloadFileState()
    data class ErrorState(val message: String) : DownloadFileState()
    data class SuccessState(val downloadedData: Any) : DownloadFileState()

}