package com.example.takenotes.Video.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.IRecyclerOnClick
import com.example.takenotes.Api.ipaddress
import com.example.takenotes.Common.Common
import com.example.takenotes.Controller.ItemTouchHelperListener
import com.example.takenotes.Model.Video
import com.example.takenotes.R
import com.example.takenotes.Video.VideoPlayActivity.VideoPlayActivity
import io.reactivex.disposables.CompositeDisposable

class VideoRecyclerAdapter(internal val context: Context, internal val video: List<Video>) :
    RecyclerView.Adapter<VideoRecyclerAdapter.Holder>(){
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_recycler_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return video.size
    }

    override fun onBindViewHolder(holder: VideoRecyclerAdapter.Holder, position: Int) {
        if (video[position].title != null){
            holder.VideoTitleView.text = video[position].title
        }else{
            holder.VideoTitleView.visibility = View.GONE
        }
        Glide.with(context).load(ipaddress.ip+video[position].video).override(200,200).into(holder.VideoImageView)
        holder.VideoUpdateView.text = video[position].updated_at

        holder.setClick(object : IRecyclerOnClick {
            override fun onLongClick(view: View, Position: Int) {

            }

            override fun onClick(view: View, position: Int) {
                Common.SelectVideo = video[position]
//                Toast.makeText(view.context,Memo[position].title + "클릭"+"("+position+")",Toast.LENGTH_SHORT).show()
                val intent = Intent(view.context, VideoPlayActivity::class.java)
                view.context.startActivity(intent)

            }

        })
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal var VideoTitleView: TextView
        internal var VideoImageView: ImageView
        internal var VideoUpdateView: TextView
        internal lateinit var iRecyclerOnClick: IRecyclerOnClick

        override fun onClick(v: View?) {
            iRecyclerOnClick.onClick(v!!, adapterPosition)

        }

        fun setClick(iRecyclerOnClick: IRecyclerOnClick) {
            this.iRecyclerOnClick = iRecyclerOnClick
        }

        init {
            VideoTitleView = itemView.findViewById(R.id.VideoTitleView) as TextView
            VideoImageView = itemView.findViewById(R.id.VideoThumbNail) as ImageView
            VideoUpdateView = itemView.findViewById(R.id.VideoUpdateView) as TextView
            itemView.setOnClickListener(this)
        }

    }

}