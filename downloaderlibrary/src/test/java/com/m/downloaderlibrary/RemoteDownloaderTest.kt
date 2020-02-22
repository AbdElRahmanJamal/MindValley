package com.m.downloaderlibrary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.datadownloader.DataDownloadedFormatter
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.remotdatatstore.RemoteDownloader
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.helper.OPEN_CONNECTION_ERROR
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito


class RemoteDownloaderTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    @Mock
    private lateinit var cashingManager: CashingManager

    @ExperimentalCoroutinesApi
    val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var viewStatObserver: Observer<Any>

    private lateinit var remoteDownloader: RemoteDownloader

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_download_remote_data_success() {

        remoteDownloader = PowerMockito.spy(RemoteDownloader(dataDownloadedFormatter, DownloadDataType.IMAGE, testCoroutineDispatcher))

        runBlockingTest {

            Mockito.`when`(dataDownloadedFormatter.formatterDataFlow).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                emit(DownloadFileState.SuccessState(value))
            })

            remoteDownloader.startDownloading(cashingManager, key)

            remoteDownloader.remoteData.observeForever(viewStatObserver)

            Mockito.verify(dataDownloadedFormatter).formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.IMAGE)
            Mockito.verify(viewStatObserver).onChanged(DownloadFileState.SuccessState(value))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_download_remote_data_failed() {
        remoteDownloader = RemoteDownloader(dataDownloadedFormatter, DownloadDataType.IMAGE, testCoroutineDispatcher)

        runBlockingTest {

            Mockito.`when`(dataDownloadedFormatter.formatterDataFlow).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                emit(DownloadFileState.ErrorState(OPEN_CONNECTION_ERROR))
            })

            remoteDownloader.startDownloading(cashingManager, key)

            remoteDownloader.remoteData.observeForever(viewStatObserver)
            Mockito.verify(dataDownloadedFormatter).formatDownloadedDataBaseOnDownloadDataType(DownloadDataType.IMAGE)
            Mockito.verify(viewStatObserver).onChanged(DownloadFileState.ErrorState(OPEN_CONNECTION_ERROR))
        }
    }
}