package com.m.downloaderlibrary

import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.helper.INVALID_URL
import com.m.downloaderlibrary.helper.OPEN_CONNECTION_ERROR
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


fun <T> anyOrNull(): T = Mockito.any<T>()

const val VALID_URL = "https://pastebin.com/raw/wgkJgazE"
const val IN_VALID_URL = ""

class BaseFileDownloaderTest {

    private lateinit var baseFileDownloader: BaseFileDownloader

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun test_valid_url_emit_open_connection_error_error() = runBlockingTest {

        baseFileDownloader = BaseFileDownloader(VALID_URL)

        baseFileDownloader.downloadFromURL()

        val downloadedData = baseFileDownloader.getDownloadedData().take(2).toList()

        assertEquals(DownloadFileState.LoadingState, downloadedData[0])
        assertEquals(DownloadFileState.ErrorState(OPEN_CONNECTION_ERROR), downloadedData[1])

    }

    @Test
    fun test_in_valid_url_emit_in_valid_URL_error() = runBlockingTest {

        baseFileDownloader = BaseFileDownloader(IN_VALID_URL)

        baseFileDownloader.downloadFromURL()

        val downloadedData = baseFileDownloader.getDownloadedData().take(2).toList()

        assertEquals(DownloadFileState.LoadingState, downloadedData[0])
        assertEquals(DownloadFileState.ErrorState(INVALID_URL), downloadedData[1])
    }

    @Test
    fun test_valid_url_emit_success_state_with_downloaded_data() = runBlockingTest {

        val urlConnection = mock(URL::class.java)
        val openStream = mock(InputStream::class.java)
        val httpsURLConnection = mock(URLConnection::class.java)
        PowerMockito.whenNew(URL::class.java).withArguments(VALID_URL).thenReturn(urlConnection)
        Mockito.`when`(urlConnection.openConnection()).thenReturn(httpsURLConnection)
        Mockito.`when`(urlConnection.openStream()).thenReturn(openStream)

        baseFileDownloader = BaseFileDownloader(VALID_URL)
        baseFileDownloader.downloadFromURL()


        val downloadedData = baseFileDownloader.getDownloadedData().take(2).toList()
        assertEquals(DownloadFileState.LoadingState, downloadedData[0])

    }
}
