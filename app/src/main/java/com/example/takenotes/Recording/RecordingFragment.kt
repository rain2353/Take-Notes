package com.example.takenotes.Recording

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
import com.example.takenotes.Recording.Adapter.AudioRecyclerAdapter
import com.example.takenotes.Recording.VoiceRecodingActivity.VoiceRecodingActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recording.*

class RecordingFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 화면에 보이는 플러스 모양의 FloatingActionButton 을 누르면 음성 녹음을 하고 업로드할수 있는 액티비티로 이동한다.
        this.VoiceRecodingButton.setOnClickListener {
            val intent = Intent(context, VoiceRecodingActivity::class.java)
            startActivity(intent)
        }

        // 리사이클러뷰 레이아웃매니저를 설정한다.
        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true   // 리사이클러뷰 역순 출력.
        lm.stackFromEnd = true    // 리사이클러뷰 역순 출력.
        RecodingRecyclerView.layoutManager = lm

        super.onViewCreated(view, savedInstanceState)
    }

    // 사용자의 이메일로 MySQL 에 저장된 DB를 확인한 후, 불러와서 사용자에게 리스트를 보여준다.
    private fun AudioList(email: String?) {
        compositeDisposable.add(myAPI.AudioList(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Audio ->
                RecodingRecyclerView.adapter = AudioRecyclerAdapter(context!!, Audio)
                (RecodingRecyclerView.adapter as AudioRecyclerAdapter).notifyDataSetChanged()
            }
                , { thr ->
                    Log.d("AudioRecyclerView", thr.message.toString())
                }

            ))
    }

    override fun onStart() {    // 음성녹음을 업로드하고 나왔을때 업로드한 녹음 파일이 리스트 맨위에서 바로 확인할수 있도록 한다.
        AudioList(Common.UserInfomation?.email)
        super.onStart()
    }
}
