package com.m.downloaderlibrary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.m.downloaderlibrary.datadownloader.BaseFileDownloader
import com.m.downloaderlibrary.datadownloader.DataDownloadedFormatter
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.ByteArrayOutputStream


class DataDownloadedFormatterTest {

    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    @Mock
    private lateinit var baseFileDownloader: BaseFileDownloader

    @Mock
    private lateinit var bitmap: Bitmap

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        dataDownloadedFormatter = DataDownloadedFormatter(baseFileDownloader)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun ` test valid url emit error`() = runBlockingTest {

        Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
            emit(DownloadFileState.LoadingState)
            emit(DownloadFileState.ErrorState(ArgumentMatchers.anyString()))
        })
        dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.JSON)
        val downloadedData = dataDownloadedFormatter.formatterDataFlow.take(2).toList()

        Assert.assertEquals(DownloadFileState.LoadingState, downloadedData[0])
        Assert.assertEquals(
                DownloadFileState.ErrorState(ArgumentMatchers.anyString()),
                downloadedData[1]
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_valid_url_emit_success_IMAGE_type() {
        val downloadFileResult = DownloadFileState.SuccessState(ByteArrayOutputStream())


        runBlockingTest {

            Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                emit(downloadFileResult)
            })


            dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.IMAGE)

            val downloadedData = dataDownloadedFormatter.formatterDataFlow.take(2).toList()
            Assert.assertEquals(DownloadFileState.LoadingState, downloadedData[0])
            Assert.assertTrue(downloadedData[1] is DownloadFileState.SuccessState)
            val successState = downloadedData[1] as DownloadFileState.SuccessState
            Assert.assertTrue(successState.downloadedData is ByteArray)
            Mockito.verify(baseFileDownloader).getDownloadedData()
            Mockito.verify(baseFileDownloader).downloadFromURL()

        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_valid_url_emit_success_JSON_type() {
        val downloadFileResult =
                DownloadFileState.SuccessState(ByteArrayOutputStream())

        runBlockingTest {

            Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                delay(500)
                emit(downloadFileResult)
            })
            dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.JSON)
            val downloadedData = dataDownloadedFormatter.formatterDataFlow.take(2).toList()

            Assert.assertEquals(DownloadFileState.LoadingState, downloadedData[0])
            Assert.assertTrue(downloadedData[1] is DownloadFileState.SuccessState)
            val successState = downloadedData[1] as DownloadFileState.SuccessState

            Assert.assertTrue(successState.downloadedData is String)

            Mockito.verify(baseFileDownloader).getDownloadedData()
            Mockito.verify(baseFileDownloader).downloadFromURL()

        }
    }
}