package com.m.downloaderlibrary

import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class CashingManagerTest {

    private lateinit var cashingManager: CashingManager

    @Mock
    private lateinit var memoryCashingFactory: MemoryCashingFactory

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        cashingManager = CashingManager(memoryCashingFactory)
    }

    @Test
    fun `is downloaded data found in cashing manager`() {

        Mockito.`when`(memoryCashingFactory.isDownloadedDataFoundInCashingManager(key)).thenReturn(true)

        cashingManager.isDownloadedDataFoundInCashingManager(key)

        Mockito.verify(memoryCashingFactory).isDownloadedDataFoundInCashingManager(key)

        Assert.assertTrue(cashingManager.isDownloadedDataFoundInCashingManager(key))
    }

    @Test
    fun `is downloaded data not found in cashing manager`() {

        Mockito.`when`(memoryCashingFactory.isDownloadedDataFoundInCashingManager(key)).thenReturn(false)

        cashingManager.isDownloadedDataFoundInCashingManager(key)

        Mockito.verify(memoryCashingFactory).isDownloadedDataFoundInCashingManager(key)

        Assert.assertFalse(cashingManager.isDownloadedDataFoundInCashingManager(key))
    }

    @Test
    fun `get data from cash manager`() {

        Mockito.`when`(memoryCashingFactory.getDownloadedDataFromCash(key)).thenReturn(value)

        cashingManager.getDownloadedDataFromCash(key)

        Mockito.verify(memoryCashingFactory).getDownloadedDataFromCash(key)

        Assert.assertEquals(cashingManager.getDownloadedDataFromCash(key), value)
    }

    @Test
    fun `put data into cash manager success`() {

        Mockito.`when`(memoryCashingFactory.isDownloadedDataFoundInCashingManager(key)).thenReturn(false)

        cashingManager.putDownloadedDataIntoCash(key, value)

        Mockito.verify(memoryCashingFactory).isDownloadedDataFoundInCashingManager(key)
        Mockito.verify(memoryCashingFactory).putDownloadedDataIntoCash(key, value)

    }

    @Test
    fun `clear cash success`() {
        Mockito.`when`(memoryCashingFactory.clearCashedData()).thenReturn(true)
        cashingManager.clearCashedData()
        Mockito.verify(memoryCashingFactory).clearCashedData()
        Assert.assertTrue(cashingManager.clearCashedData())
    }

    @Test
    fun `clear cash fail`() {
        Mockito.`when`(memoryCashingFactory.clearCashedData()).thenReturn(false)
        cashingManager.clearCashedData()
        Mockito.verify(memoryCashingFactory).clearCashedData()
        Assert.assertFalse(cashingManager.clearCashedData())
    }

}