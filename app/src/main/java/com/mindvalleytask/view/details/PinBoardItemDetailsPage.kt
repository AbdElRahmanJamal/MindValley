package com.mindvalleytask.view.details

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.m.downloaderlibrary.cashingmanager.CashingManager
import com.m.downloaderlibrary.cashingmanager.MemoryCashingFactory
import com.m.downloaderlibrary.datadownloader.BaseFileDownloader
import com.m.downloaderlibrary.datadownloader.DataDownloadedFormatter
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.localdatatstore.LocalReaderFromMemoryCash
import com.m.downloaderlibrary.datadownloader.downloaderdatastore.remotdatatstore.RemoteDownloader
import com.m.downloaderlibrary.datadownloader.downloaderrepository.DownloaderRepository
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.m.downloaderlibrary.helper.DownloadDataType
import com.mindvalleytask.R
import com.mindvalleytask.model.BaseResponse
import com.mindvalleytask.view.BaseScreenFragment
import kotlinx.android.synthetic.main.pin_board_item_details_page_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PinBoardItemDetailsPage : BaseScreenFragment() {

    private lateinit var pinBoardItem: BaseResponse
    private lateinit var dataDownloadedFormatter: DataDownloadedFormatter

    override fun getLayoutId() = R.layout.pin_board_item_details_page_fragment
    override fun getScreenTitle() = resources.getString(R.string.details_title)

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            setFragmentContentVisibility(View.VISIBLE)
            setLoadingIndicatorVisibility(View.GONE)
            getDateFromSafeArgs()
            mapDataToViews()

            btn_view_profile.setOnClickListener {
                navigateToProfilePage()
            }
            downloadUserProfileImage()
        }
    }

    private fun getDateFromSafeArgs() {
        arguments?.let {
            pinBoardItem = PinBoardItemDetailsPageArgs.fromBundle(it).pinBoardItem
        }
        dataDownloadedFormatter =
                DataDownloadedFormatter(BaseFileDownloader(pinBoardItem.user.profile_image.large))
    }

    private fun mapDataToViews() {
        tv_title.text = String.format("Image User Name - %S", pinBoardItem.user.name)

        tv_image_dimensions.text =
                String.format("Image Dimensions - %d x %d", pinBoardItem.width, pinBoardItem.height)

        tv_image_published_date.text =
                String.format("Created Date - %S", pinBoardItem.created_at)
    }

    @ExperimentalCoroutinesApi
    private fun downloadUserProfileImage() {

        val cashingManager by lazy { CashingManager.getInstance(MemoryCashingFactory) }
        val baseDownloader by lazy { BaseFileDownloader(pinBoardItem.user.profile_image.large) }
        val dataDownloadedFormatter by lazy { DataDownloadedFormatter(baseDownloader) }
        val remoteDownloader by lazy { RemoteDownloader(dataDownloadedFormatter, DownloadDataType.IMAGE) }
        val localReaderFromMemoryCash by lazy { LocalReaderFromMemoryCash() }
        val downloaderRepository by lazy {
            DownloaderRepository(cashingManager, remoteDownloader, localReaderFromMemoryCash, pinBoardItem.user.profile_image.large)
        }


        val imageDownloader by lazy {
            ImageDownloader(downloaderRepository,
                    R.drawable.ic_launcher_background,
                    R.drawable.error,
                    iv_image_view)
        }
        imageDownloader.downloadImage()
    }

    private fun navigateToProfilePage() {
        val goToProfilePage =
                PinBoardItemDetailsPageDirections.actionDetailsPageToUserProfilePage(pinBoardItem.user.links.html)
        Navigation.findNavController(view!!).navigate(goToProfilePage)
    }

}
