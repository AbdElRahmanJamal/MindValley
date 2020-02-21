package com.mindvalleytask.view.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.pin_board_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PinBoardListAdapter(private val pinBoardItemClickListener: PinBoardItemClickLicener) :
        RecyclerView.Adapter<PinBoardListAdapter.HomeViewHolder>() {

    private var pinBoardList: MutableList<BaseResponse> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = inflater.context
        val view = inflater.inflate(R.layout.pin_board_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = pinBoardList[position]
        holder.pinTextLikes.text = String.format("%d", item.likes)
        holder.pinTextTitle.text = context.getString(
                R.string.clicked,
                item.user.name
        )

        holder.pinTextLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)

        downloadImage(item, holder)

        holder.itemView.setOnClickListener {
            pinBoardItemClickListener.onPinBoardItemClickLicener(item)
        }
    }

    @ExperimentalCoroutinesApi
    private fun downloadImage(
            item: BaseResponse,
            holder: HomeViewHolder
    ) {

        val cashingManager by lazy { CashingManager.getInstance(MemoryCashingFactory) }
        val baseDownloader by lazy { BaseFileDownloader(item.urls.thumb) }
        val dataDownloadedFormatter by lazy { DataDownloadedFormatter(baseDownloader) }
        val remoteDownloader by lazy { RemoteDownloader(dataDownloadedFormatter, DownloadDataType.IMAGE, cashingManager) }
        val localReaderFromMemoryCash by lazy { LocalReaderFromMemoryCash(cashingManager, item.urls.thumb) }
        val downloaderRepository by lazy {
            DownloaderRepository(cashingManager, remoteDownloader, localReaderFromMemoryCash, item.urls.thumb)
        }


        val imageDownloader by lazy {
            ImageDownloader(downloaderRepository,
                    R.drawable.ic_launcher_background,
                    R.drawable.error,
                    holder.pinImage)
        }
        imageDownloader.downloadImage()
    }

    internal fun setPinItems(items: List<BaseResponse>) {
        this.pinBoardList.clear()
        this.pinBoardList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return pinBoardList.size
    }

    inner class HomeViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        val pinImage: ImageView = itemView.iv_image_thumbnail
        val pinTextTitle: TextView = itemView.tv_title
        val pinTextLikes: TextView = itemView.tv_likes

    }
}
