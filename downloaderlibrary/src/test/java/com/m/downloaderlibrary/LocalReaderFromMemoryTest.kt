package com.m.downloaderlibrary

import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore.LocalReaderFromMemoryCash
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LocalReaderFromMemoryTest {

    @Mock
    private lateinit var cashingManager: CashingManager

    private lateinit var localReaderFromMemoryCash: LocalReaderFromMemoryCash

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        localReaderFromMemoryCash = LocalReaderFromMemoryCash()

    }

    @Test
    fun test_read_from_cash() {

        Mockito.`when`(cashingManager.isDownloadedDataFoundInCashingManager("VALID_URL"))
                .thenReturn(true)
        Mockito.`when`(cashingManager.getDownloadedDataFromCash("VALID_URL")).thenReturn("DATA")

        localReaderFromMemoryCash.readDataFromCash(cashingManager, "VALID_URL")
    }
}