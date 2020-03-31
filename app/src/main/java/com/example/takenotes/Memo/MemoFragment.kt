package com.example.takenotes.Memo

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.Controller.ItemTouchHelperCallback
import com.example.takenotes.Memo.WriteMemoActivity.WriteMemoActivity
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_memo.*
import com.example.takenotes.Memo.Adapter.MemoRecyclerAdapter



class MemoFragment : Fragment() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    var helper: ItemTouchHelper? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 화면에 보이는 플러스 모양의 FloatingActionButton 을 누르면 메모 작성하는 액티비티로 이동한다.
        this.WriteMemoButton.setOnClickListener {
            val intent = Intent(context, WriteMemoActivity::class.java)
            startActivity(intent)
        }

        // 리사이클러뷰 레이아웃매니저를 설정한다.
        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true   // 리사이클러뷰 역순 출력.
        lm.stackFromEnd = true    // 리사이클러뷰 역순 출력.
        MemoRecyclerView.layoutManager = lm



    }


    // 사용자의 이메일로 MySQL 에 저장된 DB를 확인한 후, 불러와서 사용자에게 리스트를 보여준다.
    private fun MemoList(email: String?) {
        compositeDisposable.add(myAPI.MemoList(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Memo ->
                //ItemTouchHelper 생성
                val memoSwipeClickListener = MemoRecyclerAdapter(context!!, Memo)
                helper = ItemTouchHelper( ItemTouchHelperCallback(memoSwipeClickListener))
                helper!!.attachToRecyclerView(MemoRecyclerView)
                MemoRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration(){
                    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                        helper!!.onDraw(c, parent, state)
                    }
                })
                MemoRecyclerView.adapter = memoSwipeClickListener
                (MemoRecyclerView.adapter as MemoRecyclerAdapter).notifyDataSetChanged()
            }
                , { thr ->
                    Log.d("MemoRecyclerView", thr.message.toString())
                }

            ))
    }

    override fun onStart() {    // 메모를 작성하고 나왔을때 작성한 메모가 리스트 맨위에서 볼수 있도록 한다.
        MemoList(Common.UserInfomation?.email)
        super.onStart()
    }


}


