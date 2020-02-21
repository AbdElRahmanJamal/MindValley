package com.mindvalleytask

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.view.details.PinBoardItemDetailsPageViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class PinBoardItemDetailsPageViewModelTest {

    private lateinit var pinBoardItemDetailsPageViewModel: PinBoardItemDetailsPageViewModel

    @Mock
    private lateinit var imageDownloader: ImageDownloader

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        pinBoardItemDetailsPageViewModel = PinBoardItemDetailsPageViewModel(imageDownloader)
    }

    @Test
    fun test_on_fetch_image_success() {

        val mutableLiveDataState = MutableLiveData<DownloadFileState>()
        val successState = DownloadFileState.SuccessState(ContentURLResult(createByteArray()))

        mutableLiveDataState.value = successState

        Mockito.`when`(imageDownloader.getDownloadedDateState()).thenReturn(mutableLiveDataState)
        val remoteData = pinBoardItemDetailsPageViewModel.getDownloadImageState()
        //then
        Assert.assertEquals(remoteData.getOrAwaitValue(), successState)
        Mockito.verify(imageDownloader).getDownloadedDateState()
        Mockito.verify(imageDownloader).startDownloading()
    }

    @Test
    fun test_on_fetch_image_failed() {

        val mutableLiveDataState = MutableLiveData<DownloadFileState>()
        val errorState = DownloadFileState.ErrorState("ERROR_MESSAGE")

        mutableLiveDataState.value = errorState

        Mockito.`when`(imageDownloader.getDownloadedDateState()).thenReturn(mutableLiveDataState)
        val remoteData = pinBoardItemDetailsPageViewModel.getDownloadImageState()
        //then
        Assert.assertEquals(remoteData.getOrAwaitValue(), errorState)
        Mockito.verify(imageDownloader).getDownloadedDateState()
        Mockito.verify(imageDownloader).startDownloading()
    }

}