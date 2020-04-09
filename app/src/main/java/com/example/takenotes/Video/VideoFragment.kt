package com.example.takenotes.Video


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.example.takenotes.Video.Adapter.VideoRecyclerAdapter
import com.example.takenotes.Video.VideoUploadActivity.VideoUploadActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video.*


class VideoFragment : Fragment() {
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 화면에 보이는 플러스 모양의 FloatingActionButton 을 누르면 동영상을 업로드할수 있는 액티비티로 이동한다.
        this.VideoUploadButton.setOnClickListener {
            val intent = Intent(context, VideoUploadActivity::class.java)
            startActivity(intent)
        }
        // 리사이클러뷰 레이아웃매니저를 설정한다.
        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true   // 리사이클러뷰 역순 출력.
        lm.stackFromEnd = true    // 리사이클러뷰 역순 출력.
        VideoRecyclerView.layoutManager = lm

        super.onViewCreated(view, savedInstanceState)
    }
    // 사용자의 이메일로 MySQL 에 저장된 DB를 확인한 후, 불러와서 사용자에게 리스트를 보여준다.
    private fun VideoList(email: String?) {
        compositeDisposable.add(myAPI.VideoList(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Video ->
                VideoRecyclerView.adapter = VideoRecyclerAdapter(context!!, Video)
                (VideoRecyclerView.adapter as VideoRecyclerAdapter).notifyDataSetChanged()
            }
                , { thr ->
                    Log.d("VideoRecyclerView", thr.message.toString())
                }

            ))
    }

    override fun onStart() {    // 동영상을 업로드하고 나왔을때 업로드한 동영상이 리스트 맨위에서 볼수 있도록 한다.
        VideoList(Common.UserInfomation?.email)
        super.onStart()
    }
}
