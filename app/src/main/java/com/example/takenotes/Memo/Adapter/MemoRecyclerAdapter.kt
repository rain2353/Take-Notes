package com.example.takenotes.Memo.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.IRecyclerOnClick
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.Controller.ItemTouchHelperListener
import com.example.takenotes.Memo.ReadMemoActivity.ReadMemoActivity
import com.example.takenotes.Model.Memo
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MemoRecyclerAdapter(internal val context: Context, internal val memo: List<Memo>) :
    RecyclerView.Adapter<MemoRecyclerAdapter.Holder>(),
    ItemTouchHelperListener {
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.memo_recycler_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return memo.size
    }

    override fun onBindViewHolder(holder: MemoRecyclerAdapter.Holder, position: Int) {
        holder.MemoTitleView.text = memo[position].title
        holder.MemoContentView.text = memo[position].content
        holder.MemoUpdateView.text = memo[position].updated_at

        holder.setClick(object : IRecyclerOnClick {
            override fun onLongClick(view: View, Position: Int) {

            }

            override fun onClick(view: View, position: Int) {
                Common.SelectMemo = memo[position]
//                Toast.makeText(view.context,Memo[position].title + "클릭"+"("+position+")",Toast.LENGTH_SHORT).show()
                val intent = Intent(view.context, ReadMemoActivity::class.java)
                view.context.startActivity(intent)

            }

        })
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal var MemoTitleView: TextView
        internal var MemoContentView: TextView
        internal var MemoUpdateView: TextView
        internal lateinit var iRecyclerOnClick: IRecyclerOnClick

        override fun onClick(v: View?) {
            iRecyclerOnClick.onClick(v!!, adapterPosition)

        }

        fun setClick(iRecyclerOnClick: IRecyclerOnClick) {
            this.iRecyclerOnClick = iRecyclerOnClick
        }

        init {
            MemoTitleView = itemView.findViewById(R.id.MemoTitleView) as TextView
            MemoContentView = itemView.findViewById(R.id.MemoContentView) as TextView
            MemoUpdateView = itemView.findViewById(R.id.MemoUpdateView) as TextView
            itemView.setOnClickListener(this)
        }

    }

    override fun onItemMove(from_position: Int, to_position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemSwipe(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder) {
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

//        Log.d("MemoDeleteClick", memo[position].num.toString())
        DeleteMemo(memo[position].num)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,memo.size)
    }

    // 메모 삭제하기.
    fun DeleteMemo(num: Int?) {
        compositeDisposable.add(myAPI.DeleteMemo(num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Toast.makeText(context, "메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("notice_delete", message.toString())


            }
                , { thr ->
                    Toast.makeText(context, "메모를 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("notice_delete", thr.message.toString())

                }

            ))
    }

}