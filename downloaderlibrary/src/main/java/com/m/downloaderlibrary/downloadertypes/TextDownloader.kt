package com.m.downloaderlibrary.downloadertypes

import com.m.downloaderlibrary.datadownloader.downloaderrepository.DownloaderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TextDownloader(private val downloaderRepository: DownloaderRepository
) {
    @ExperimentalCoroutinesApi
    fun downloadText() = downloaderRepository.getDownloadedData()

}