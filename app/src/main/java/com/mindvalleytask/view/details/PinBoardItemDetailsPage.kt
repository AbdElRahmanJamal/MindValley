package com.mindvalleytask.view.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.m.downloaderlibrary.helper.HelperMethods
import com.m.downloaderlibrary.model.ContentURLResult
import com.m.downloaderlibrary.model.DownloadFileState
import com.mindvalleytask.R
import com.mindvalleytask.model.BaseResponse
import com.mindvalleytask.view.BaseScreenFragment
import kotlinx.android.synthetic.main.pin_board_item_details_page_fragment.*

class PinBoardItemDetailsPage : BaseScreenFragment() {

    private lateinit var pinBoardItem: BaseResponse
    private lateinit var viewModelPinBoardItem: PinBoardItemDetailsPageViewModel
    private lateinit var pinBoardItemDetailsPageViewModelFactory: PinBoardItemDetailsPageViewModelFactory
    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    override fun getLayoutId() = R.layout.pin_board_item_details_page_fragment
    override fun getScreenTitle() = resources.getString(R.string.details_title)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getDateFromSafeArgs()
        initViewModel()
        mapDataToViews()

        viewModelPinBoardItem.getDownloadImageState().observeForever {
            when (it) {
                is DownloadFileState.LoadingState -> onLoading()
                is DownloadFileState.SuccessState -> onDataFetchedSuccessfully(it.downloadFileResult)
                is DownloadFileState.ErrorState -> onError()
            }
        }
        btn_view_profile.setOnClickListener {
            navigateToProfilePage()
        }
    }

    private fun navigateToProfilePage() {
        val goToProfilePage =
            PinBoardItemDetailsPageDirections.actionDetailsPageToUserProfilePage(pinBoardItem.user.links.html)
        Navigation.findNavController(view!!).navigate(goToProfilePage)
    }

    private fun getDateFromSafeArgs() {
        arguments?.let {
            pinBoardItem = PinBoardItemDetailsPageArgs.fromBundle(it).pinBoardItem
        }
        dataDownloadedFormatter =
            DataDownloadedFormatter(BaseFileDownloader(pinBoardItem.user.profile_image.large))
    }

    private fun initViewModel() {

        pinBoardItemDetailsPageViewModelFactory =

            PinBoardItemDetailsPageViewModelFactory(
                ImageDownloader(dataDownloadedFormatter)
            )

        viewModelPinBoardItem = ViewModelProvider(
            this, pinBoardItemDetailsPageViewModelFactory
        ).get(PinBoardItemDetailsPageViewModel::class.java)
    }

    private fun onLoading() {
        setLoadingIndicatorVisibility(View.VISIBLE)
        setFragmentContentVisibility(View.GONE)
    }

    private fun onDataFetchedSuccessfully(downloadFileResult: ContentURLResult) {
        val stringToBitMap =
            HelperMethods.createBitmapFromByteArray(downloadFileResult.downloadedData as ByteArray)
        iv_image_view!!.setImageBitmap(stringToBitMap)
        setLoadingIndicatorVisibility(View.GONE)
        setFragmentContentVisibility(View.VISIBLE)
    }

    private fun onError() {
        iv_image_view!!.setBackgroundResource(R.drawable.error)
        setLoadingIndicatorVisibility(View.GONE)
        setFragmentContentVisibility(View.VISIBLE)
    }

    private fun mapDataToViews() {
        tv_title.text = String.format("Image User Name - %S", pinBoardItem.user.name)

        tv_image_dimensions.text =
            String.format("Image Dimensions - %d x %d", pinBoardItem.width, pinBoardItem.height)

        tv_image_published_date.text =
            String.format("Created Date - %S", pinBoardItem.created_at)
    }
}
