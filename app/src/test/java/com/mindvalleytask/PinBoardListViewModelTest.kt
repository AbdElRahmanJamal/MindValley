package com.mindvalleytask

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.data.DownloadJsonFile
import com.mindvalleytask.view.home.PinBoardListPageViewModel
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream

class PinBoardListViewModelTest {


    private lateinit var pinBoardListPageViewModel: PinBoardListPageViewModel

    @Mock
    private lateinit var downloadJsonFile: DownloadJsonFile

    @Mock
    lateinit var memoryCashingFactory: MemoryCashingFactory

    private lateinit var cashingManager: CashingManager

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        pinBoardListPageViewModel = PinBoardListPageViewModel(downloadJsonFile)
        cashingManager = CashingManager(memoryCashingFactory)
    }

    @Test
    fun test_on_fetch_json_success() {

        val mutableLiveDataState = MutableLiveData<DownloadFileState>()
        val successState = DownloadFileState.SuccessState(ContentURLResult(createByteArray()))

        mutableLiveDataState.value = successState

        Mockito.`when`(downloadJsonFile.getDownloadedJson()).thenReturn(mutableLiveDataState)
        val remoteData = pinBoardListPageViewModel.getDownloadedJson()
        //then
        Assert.assertEquals(remoteData.getOrAwaitValue(), successState)
        Mockito.verify(downloadJsonFile).getDownloadedJson()
    }

    @Test
    fun test_on_fetch_json_error() {

        val mutableLiveDataState = MutableLiveData<DownloadFileState>()
        val errorState = DownloadFileState.ErrorState("ErrorMessage")

        mutableLiveDataState.value = DownloadFileState.LoadingState
        mutableLiveDataState.value = errorState

        Mockito.`when`(downloadJsonFile.getDownloadedJson()).thenReturn(mutableLiveDataState)
        val remoteData = pinBoardListPageViewModel.getDownloadedJson()
        //then
        Assert.assertEquals(remoteData.getOrAwaitValue(), errorState)
        Mockito.verify(downloadJsonFile).getDownloadedJson()
    }


    @Test
    fun clear_cashSuccess() {
        Mockito.`when`(memoryCashingFactory.clearCashedData()).thenReturn(true)
        pinBoardListPageViewModel.clearCash()
        assertTrue(cashingManager.clearCashedData())
    }

    @Test
    fun clear_cashFailed() {
        Mockito.`when`(memoryCashingFactory.clearCashedData()).thenReturn(false)
        pinBoardListPageViewModel.clearCash()
        assertFalse(cashingManager.clearCashedData())
    }

}

fun createByteArray(): kotlin.ByteArray {
    val bout = ByteArrayOutputStream()
    for (i in 0..9) {
        bout.write((Math.random() * 100).toByte().toInt())
    }
    return bout.toByteArray()
}
