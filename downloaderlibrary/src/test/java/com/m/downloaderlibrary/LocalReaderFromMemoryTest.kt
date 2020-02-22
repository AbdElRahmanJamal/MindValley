package com.m.downloaderlibrary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore.LocalReaderFromMemoryCash
import com.m.downloaderlibrary.helper.DATA_NOT_FOUND_IN_CASH
import com.m.downloaderlibrary.model.DownloadFileState
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


const val key = "VALID_URL"
const val value = "DATA"
const val emptyValue = ""

class LocalReaderFromMemoryTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() //for live data test

    @Mock
    private lateinit var cashingManager: CashingManager
    @Mock
    private lateinit var viewStatObserver: Observer<Any>

    private lateinit var localReaderFromMemoryCash: LocalReaderFromMemoryCash

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        localReaderFromMemoryCash = LocalReaderFromMemoryCash()

    }

    @Test
    fun test_read_from_cash_data_in_cash() = runBlockingTest {

        Mockito.`when`(cashingManager.getDownloadedDataFromCash(key))
                .thenReturn(value)

        localReaderFromMemoryCash.readDataFromCash(cashingManager, key)

        val successState = localReaderFromMemoryCash.localData

        Assert.assertEquals(successState.value, DownloadFileState.SuccessState(value))

    }

    @Test
    fun test_read_from_cash_data_in_cash_is_empty() = runBlockingTest {

        Mockito.`when`(cashingManager.getDownloadedDataFromCash(key))
                .thenReturn(emptyValue)

        localReaderFromMemoryCash.readDataFromCash(cashingManager, key)

        val successState = localReaderFromMemoryCash.localData

        Assert.assertEquals(successState.value, DownloadFileState.ErrorState(DATA_NOT_FOUND_IN_CASH))

    }
}