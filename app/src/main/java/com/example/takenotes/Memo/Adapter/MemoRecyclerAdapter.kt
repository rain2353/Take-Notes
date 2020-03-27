package com.example.takenotes.Memo.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.Api.IRecyclerOnClick
import com.example.takenotes.Common.Common
import com.example.takenotes.Model.Memo
import com.example.takenotes.R


class MemoRecyclerAdapter(internal val context: Context,internal val Memo: List<Memo>) : RecyclerView.Adapter<MemoRecyclerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.memo_recycler_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return Memo.size
    }

    override fun onBindViewHolder(holder: MemoRecyclerAdapter.Holder, position: Int) {
//        Picasso.get().load(Memo[position].notice_image).resize(100,100).into(holder.MemoImage)
//        holder.MemoImage.visibility = View.GONE
        holder.MemoTitleView.text = Memo[position].title
        holder.MemoContentView.text = Memo[position].content
        holder.MemoUpdateView.text = Memo[position].updated_at

        holder.setClick(object : IRecyclerOnClick {
            override fun onLongClick(view: View, Position: Int) {

            }

            override fun onClick(view: View, position: Int) {
                Common.SelectMemo = Memo[position]
                Toast.makeText(view.context,Memo[position].title + "클릭"+"("+position+")",Toast.LENGTH_SHORT).show()
//                val intent = Intent(view.context, Notice_Text::class.java)
//                intent.putExtra("num",position)
//
//                view.context.startActivity(intent)

            }

        })
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal var MemoImage: ImageView
        internal var MemoTitleView: TextView
        internal var MemoContentView: TextView
        internal var MemoUpdateView: TextView
        internal lateinit var iRecyclerOnClick: IRecyclerOnClick

        override fun onClick(v: View?) {
            iRecyclerOnClick.onClick(v!!,adapterPosition)

        }
        fun setClick(iRecyclerOnClick: IRecyclerOnClick){
            this.iRecyclerOnClick = iRecyclerOnClick
        }
        init {
            MemoImage = itemView.findViewById(R.id.MemoImage) as ImageView
            MemoTitleView = itemView.findViewById(R.id.MemoTitleView) as TextView
            MemoContentView = itemView.findViewById(R.id.MemoContentView) as TextView
            MemoUpdateView = itemView.findViewById(R.id.MemoUpdateView) as TextView
            itemView.setOnClickListener(this)
        }

    }

}