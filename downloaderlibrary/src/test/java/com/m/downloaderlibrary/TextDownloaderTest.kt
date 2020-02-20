package com.m.downloaderlibrary

import android.graphics.BitmapFactory
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.m.downloaderlibrary.downloadertypes.TextDownloader
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.helper.HelperMethods
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(HelperMethods::class, BitmapFactory::class, ImageDownloader::class)
class TextDownloaderTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainRule(this)
    @Mock
    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    @Mock
    private lateinit var cashingManager: CashingManager


    @Mock
    private lateinit var baseFileDownloader: BaseFileDownloader

    @Mock
    private lateinit var downloader: TextDownloader

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        downloader = Mockito.spy(
            TextDownloader(
                dataDownloadedFormatter = dataDownloadedFormatter,
                cashingManager = cashingManager
            )
        )
    }

    @Test
    fun test_valid_url_read_from_cash() {

        mainRule.testCoroutineDispatcher.runBlockingTest {

            PowerMockito.mockStatic(HelperMethods::class.java)
            PowerMockito.mockStatic(BitmapFactory::class.java)
            `when`(dataDownloadedFormatter.baseFileDownloader).thenReturn(baseFileDownloader)
            `when`(dataDownloadedFormatter.baseFileDownloader.url).thenReturn(VALID_URL)

            `when`(cashingManager.isDownloadedDataFoundInCashingManager(VALID_URL)).thenReturn(true)

            // `when`(HelperMethods.createBitmapFromByteArray(createByteArray())).thenReturn(bitmap)
            //`when`(BitmapFactory.decodeByteArray(createByteArray(),0,10)).thenReturn(bitmap)
            //verifyStatic(HelperMethods::class.java)

            downloader.startDownloading()


            verify(cashingManager).isDownloadedDataFoundInCashingManager(VALID_URL)
            verify(cashingManager).getDownloadedDataFromCash(VALID_URL)
            verify(downloader).readDataFromCash()

        }
    }


    @Test
    fun test_valid_url_read_from_server() {

        mainRule.testCoroutineDispatcher.runBlockingTest {


            PowerMockito.mockStatic(HelperMethods::class.java)
            PowerMockito.mockStatic(BitmapFactory::class.java)
            `when`(dataDownloadedFormatter.baseFileDownloader).thenReturn(baseFileDownloader)
            `when`(dataDownloadedFormatter.baseFileDownloader.url).thenReturn(VALID_URL)

            `when`(dataDownloadedFormatter.formatterDataFlow).thenReturn(flow {
                emit(DownloadFileState.LoadingState)
                emit(DownloadFileState.ErrorState(""))
            })

            `when`(cashingManager.isDownloadedDataFoundInCashingManager(VALID_URL)).thenReturn(false)
            downloader.startDownloading()

            verify(cashingManager).isDownloadedDataFoundInCashingManager(VALID_URL)
            verify(dataDownloadedFormatter).formatterDataFlow
            verify(dataDownloadedFormatter).formatDownloadedDataBaseOnDownloadDataType(
                DownloadDataType.JSON
            )
            verify(downloader).downloadDataFromServer()


        }
    }
}
