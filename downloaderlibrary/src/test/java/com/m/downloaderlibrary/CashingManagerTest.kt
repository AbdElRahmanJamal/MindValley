package com.m.downloaderlibrary

import android.util.LruCache
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class CashingManagerTest {

    private val key = "TEST_KEY"
    private val data = "TEST_DATA"

    @Mock
    lateinit var memoryCashingFactory: MemoryCashingFactory

    private lateinit var cashingManager: CashingManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        cashingManager = CashingManager(memoryCashingFactory)
    }

    @Test
    fun test_put_downloaded_data_into_cash() {

        `when`(cashingManager.isDownloadedDataFoundInCashingManager(key)).thenReturn(false)
        cashingManager.putDownloadedDataIntoCash(key, data)
        verify(memoryCashingFactory).putDownloadedDataIntoCash(key, data)

    }

    @Test
    fun test_not_put_downloaded_data_into_cash() {

        `when`(cashingManager.isDownloadedDataFoundInCashingManager(key)).thenReturn(true)
        cashingManager.putDownloadedDataIntoCash(key, data)
        Assert.assertTrue(cashingManager.isDownloadedDataFoundInCashingManager(key))

    }

    @Test
    fun test_get_downloaded_data_from_cash() {
        `when`(memoryCashingFactory.getDownloadedDataFromCash(key)).thenReturn {
            data
        }
        cashingManager.getDownloadedDataFromCash(key)
        verify(memoryCashingFactory).getDownloadedDataFromCash(key)
    }

    @Test
    fun test_get_downloaded_data_from_cash_is_key_in_cash() {

        `when`(memoryCashingFactory.isDownloadedDataFoundInCashingManager(key)).thenReturn(true)
        Assert.assertEquals(cashingManager.isDownloadedDataFoundInCashingManager(key), true)
    }

    @Test
    fun test_get_downloaded_data_from_cash_is_key_not_in_cash() {

        `when`(memoryCashingFactory.getDownloadedDataFromCash(key)).thenReturn(null)

        Assert.assertEquals(cashingManager.isDownloadedDataFoundInCashingManager(key), false)
    }

    @Test
    fun test_clear_cash() {
        cashingManager.clearCashedData()
        verify(memoryCashingFactory).clearCashedData()
    }

    @Test
    fun test_data_inserted_successfully_in_cash() {

        `when`(cashingManager.getDownloadedDataFromCash(key)).thenReturn(data)

        cashingManager.putDownloadedDataIntoCash(key, data)

        Assert.assertEquals(cashingManager.getDownloadedDataFromCash(key), data)
    }
}