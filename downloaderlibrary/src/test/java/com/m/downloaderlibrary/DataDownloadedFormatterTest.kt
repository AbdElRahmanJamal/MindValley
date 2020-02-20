package com.m.downloaderlibrary

import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.helper.EMPTY_RESPONSE
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream

class DataDownloadedFormatterTest {

    @Mock
    private lateinit var baseFileDownloader: BaseFileDownloader

    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dataDownloadedFormatter = DataDownloadedFormatter(baseFileDownloader)
    }

    @Test
    fun test_valid_url_emit_error_json_type() = runBlockingTest {

        Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
            emit(DownloadFileState.LoadingState)
            delay(500)
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

    @Test
    fun test_valid_url_emit_empty_response_error_IMAGE_type() = runBlockingTest {

        Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
            emit(DownloadFileState.LoadingState)
            delay(500)
            emit(DownloadFileState.ErrorState(EMPTY_RESPONSE))
        })
        dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.IMAGE)
        val downloadedData = dataDownloadedFormatter.formatterDataFlow.take(2).toList()

        Assert.assertEquals(DownloadFileState.LoadingState, downloadedData[0])
        Assert.assertEquals(DownloadFileState.ErrorState(EMPTY_RESPONSE), downloadedData[1])
    }

    @Test
    fun test_valid_url_emit_success_IMAGE_type() {
        val downloadFileResult =
            DownloadFileState.SuccessState(ContentURLResult(ByteArrayOutputStream()))

        runBlockingTest {

            Mockito.`when`(baseFileDownloader.getDownloadedData()).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                delay(500)
                emit(downloadFileResult)
            })
            dataDownloadedFormatter.formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.IMAGE)
            val downloadedData = dataDownloadedFormatter.formatterDataFlow.take(2).toList()

            Assert.assertEquals(DownloadFileState.LoadingState, downloadedData[0])
            Assert.assertTrue(downloadedData[1] is DownloadFileState.SuccessState)
            val successState = downloadedData[1] as DownloadFileState.SuccessState

            Assert.assertTrue(successState.downloadFileResult.downloadedData is ByteArray)

            Mockito.verify(baseFileDownloader).getDownloadedData()
            Mockito.verify(baseFileDownloader).downloadFromURL()

        }
    }

    @Test
    fun test_valid_url_emit_success_JSON_type() {
        val downloadFileResult =
            DownloadFileState.SuccessState(ContentURLResult(ByteArrayOutputStream()))

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

            Assert.assertTrue(successState.downloadFileResult.downloadedData is String)

            Mockito.verify(baseFileDownloader).getDownloadedData()
            Mockito.verify(baseFileDownloader).downloadFromURL()

        }
    }
}