package com.example.takenotes.Recording.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.Api.IRecyclerOnClick
import com.example.takenotes.Common.Common
import com.example.takenotes.Model.Audio
import com.example.takenotes.R
import com.example.takenotes.Recording.ListenAudioActivity.ListenAudioActivity

class AudioRecyclerAdapter (internal val context: Context, internal val audio: List<Audio>) :
    RecyclerView.Adapter<AudioRecyclerAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.audio_recycler_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return audio.size
    }

    override fun onBindViewHolder(holder: AudioRecyclerAdapter.Holder, position: Int) {
        if(audio[position].title != null){
            holder.ContentTitleView.text = audio[position].title
        } else{
            holder.ContentTitleView.visibility = View.GONE
        }
        holder.AudioTitleView.text = audio[position].audio
        holder.AudioUpdateView.text = audio[position].updated_at

        holder.setClick(object : IRecyclerOnClick {
            override fun onLongClick(view: View, Position: Int) {

            }

            override fun onClick(view: View, position: Int) {
                Common.SelectAudio = audio[position]
                val intent = Intent(view.context, ListenAudioActivity::class.java)
                view.context.startActivity(intent)

            }

        })
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal var ContentTitleView: TextView
        internal var AudioTitleView: TextView
        internal var AudioUpdateView: TextView
        internal lateinit var iRecyclerOnClick: IRecyclerOnClick

        override fun onClick(v: View?) {
            iRecyclerOnClick.onClick(v!!, adapterPosition)

        }

        fun setClick(iRecyclerOnClick: IRecyclerOnClick) {
            this.iRecyclerOnClick = iRecyclerOnClick
        }

        init {
            ContentTitleView = itemView.findViewById(R.id.ContentTitleView) as TextView
            AudioTitleView = itemView.findViewById(R.id.AudioTitleView) as TextView
            AudioUpdateView = itemView.findViewById(R.id.AudioUpdateView) as TextView
            itemView.setOnClickListener(this)
        }

    }

}