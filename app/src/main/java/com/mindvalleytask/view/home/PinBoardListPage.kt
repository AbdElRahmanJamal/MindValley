package com.mindvalleytask.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.datadownloader.BaseFileDownloader
import com.m.downloaderlibrary.datadownloader.DataDownloadedFormatter
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore.LocalReaderFromMemoryCash
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.remotdatatstore.RemoteDownloader
import com.m.downloaderlibrary.datadownloader.downloaderrepository.DownloaderRepository
import com.m.downloaderlibrary.downloadertypes.TextDownloader
import com.m.downloaderlibrary.helper.DownloadDataType
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.BASE_API
import com.mindvalleytask.R
import com.mindvalleytask.Utils.Companion.convertStringToBaseModel
import com.mindvalleytask.model.BaseResponse
import com.mindvalleytask.view.BaseScreenFragment
import com.mindvalleytask.view.home.adapter.PinBoardItemClickLicener
import com.mindvalleytask.view.home.adapter.PinBoardListAdapter
import kotlinx.android.synthetic.main.pin_board_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


class PinBoardListPage : BaseScreenFragment(), PinBoardItemClickLicener {


    private lateinit var viewModel: PinBoardListPageViewModel
    private lateinit var pinBoardListPageViewModelFactory: PinBoardListPageViewModelFactory
    private lateinit var pinBoardListAdapter: PinBoardListAdapter

    private val cashingManager by lazy { CashingManager.getInstance(MemoryCashingFactory) }
    private val baseDownloader by lazy { BaseFileDownloader(BASE_API) }
    private val dataDownloadedFormatter by lazy { DataDownloadedFormatter(baseDownloader) }
    private val remoteDownloader by lazy { RemoteDownloader(dataDownloadedFormatter, DownloadDataType.JSON, cashingManager) }
    private val localReaderFromMemoryCash by lazy { LocalReaderFromMemoryCash(cashingManager, BASE_API) }
    private val downloaderRepository by lazy {
        DownloaderRepository(cashingManager, remoteDownloader, localReaderFromMemoryCash, BASE_API)
    }



    private val textDownloader by lazy {
        TextDownloader(downloaderRepository)
    }

    override fun getLayoutId() = R.layout.pin_board_list
    override fun getScreenTitle() = resources.getString(R.string.home_title)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onViewStarted()
        onClearButtonClicked()
    }

    private fun onViewStarted() {
        initRecView()
        initViewModel()
        handleSwipeToRefresh()
        getDownloadDataState()
    }

    @ExperimentalCoroutinesApi
    private fun getDownloadDataState() {
        viewModel.getDownloadedJson().observeForever {
            when (it) {
                is DownloadFileState.LoadingState -> setLoadingIndicatorVisibility(VISIBLE)
                is DownloadFileState.SuccessState -> {
                    displayDataToPinBoardList(it.downloadedData as List<BaseResponse>)
                    displayScreen()
                }
                is DownloadFileState.ErrorState -> setLoadingIndicatorVisibility(GONE)
            }
        }
    }

    private fun handleSwipeToRefresh() {
        if (pin_board_swipe_to_refresh.isRefreshing)
            pin_board_swipe_to_refresh.isRefreshing = false

        pin_board_swipe_to_refresh.isEnabled = true

        pin_board_swipe_to_refresh.setOnRefreshListener {
            onViewStarted()
        }
    }

    private fun initViewModel() {

        pinBoardListPageViewModelFactory =
                PinBoardListPageViewModelFactory(textDownloader)
        viewModel = ViewModelProvider(
                this,
                pinBoardListPageViewModelFactory
        ).get(PinBoardListPageViewModel::class.java)
    }

    private fun initRecView() {
        pinBoardListAdapter = PinBoardListAdapter(this)
        pin_board_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pinBoardListAdapter
        }
    }

    private fun onClearButtonClicked() {
        clear_cash.setOnClickListener {
            viewModel.clearCash().observeForever {
                if (it) {
                    showCashClearedSuccessfullyMessage()
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun showCashClearedSuccessfullyMessage() {
        Toast.makeText(this.context, resources.getString(R.string.clear_cache), Toast.LENGTH_LONG)
                .show()
    }

    private fun displayDataToPinBoardList(responseList: List<BaseResponse>) {
        pinBoardListAdapter.setPinItems(responseList)
    }

    private fun displayScreen() {
        setFragmentContentVisibility(VISIBLE)
        setLoadingIndicatorVisibility(GONE)
    }


    override fun onPinBoardItemClickLicener(baseResponse: BaseResponse) {
        val goToDetailsPage = PinBoardListPageDirections.actionPinboardToDetailsPage(baseResponse)
        Navigation.findNavController(view!!).navigate(goToDetailsPage)
    }

}
