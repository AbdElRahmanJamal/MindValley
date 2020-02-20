package com.m.downloaderlibrary.cashingmanager


class CashingManager(private val memoryCashingFactory: MemoryCashingFactory) {


    companion object {
        private val cashingManagerInstance: CashingManager? = null

        fun getInstance(lruCache: MemoryCashingFactory): CashingManager =
            this.cashingManagerInstance ?: CashingManager(lruCache)
    }

    fun isDownloadedDataFoundInCashingManager(key: String): Boolean {
        return memoryCashingFactory.isDownloadedDataFoundInCashingManager(key)
    }

    fun getDownloadedDataFromCash(key: String): Any {
        return memoryCashingFactory.getDownloadedDataFromCash(key)
    }

    fun putDownloadedDataIntoCash(key: String, data: Any) {
        kotlin.runCatching {
            if (!isDownloadedDataFoundInCashingManager(key))
                memoryCashingFactory.putDownloadedDataIntoCash(key, data)
        }.onFailure {
            it.printStackTrace()
        }

    }

    fun clearCashedData() {
        memoryCashingFactory.clearCashedData()
    }
}
