package com.mindvalleytask.view.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.m.downloaderlibrary.downloadertypes.BaseFileDownloader
import com.m.downloaderlibrary.downloadertypes.DataDownloadedFormatter
import com.m.downloaderlibrary.downloadertypes.ImageDownloader
import com.mindvalleytask.R
import com.mindvalleytask.model.BaseResponse
import kotlinx.android.synthetic.main.pin_board_item.view.*

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

    private fun downloadImage(
        item: BaseResponse,
        holder: HomeViewHolder
    ) {
        ImageDownloader(
            dataDownloadedFormatter = DataDownloadedFormatter(BaseFileDownloader(item.urls.thumb))
            , placeholder = R.drawable.ic_launcher_background,
            drawableOnError = R.drawable.error,
            mImageView = holder.pinImage
        ).startDownloading()
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
