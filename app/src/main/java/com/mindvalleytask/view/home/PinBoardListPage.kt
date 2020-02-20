package com.mindvalleytask.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.R
import com.mindvalleytask.Utils.Companion.convertStringToBaseModel
import com.mindvalleytask.data.BASE_API
import com.mindvalleytask.data.DownloadJsonFile
import com.mindvalleytask.model.BaseResponse
import com.mindvalleytask.view.BaseScreenFragment
import com.mindvalleytask.view.home.adapter.PinBoardItemClickLicener
import com.mindvalleytask.view.home.adapter.PinBoardListAdapter
import kotlinx.android.synthetic.main.pin_board_list.*


class PinBoardListPage : BaseScreenFragment(), PinBoardItemClickLicener {


    private lateinit var viewModel: PinBoardListPageViewModel
    private lateinit var pinBoardListPageViewModelFactory: PinBoardListPageViewModelFactory
    private lateinit var pinBoardListAdapter: PinBoardListAdapter

    private val downloadJsonFile by lazy {
        DownloadJsonFile(
            DataDownloadedFormatter(
                BaseFileDownloader(BASE_API)
            )
        )
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

    private fun getDownloadDataState() {
        viewModel.getDownloadedJson().observeForever {
            when (it) {
                is DownloadFileState.LoadingState -> setLoadingIndicatorVisibility(VISIBLE)
                is DownloadFileState.SuccessState -> {
                    val responseList =
                        convertStringToBaseModel(it.downloadFileResult.downloadedData as String) as List<BaseResponse>
                    displayDataToPinBoardList(responseList)
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
            PinBoardListPageViewModelFactory(downloadJsonFile)
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
