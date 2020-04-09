package com.example.takenotes.Picture


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
import com.example.takenotes.Picture.Adapter.PictureRecyclerAdapter
import com.example.takenotes.Picture.ImageUploadActivity.ImageUploadActivity
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_picture.*

class PictureFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 화면에 보이는 플러스 모양의 FloatingActionButton 을 누르면 이미지를 업로드할수 있는 액티비티로 이동한다.
        this.ImageUploadButton.setOnClickListener {
            val intent = Intent(context, ImageUploadActivity::class.java)
            startActivity(intent)
        }
        // 리사이클러뷰 레이아웃매니저를 설정한다.
        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true   // 리사이클러뷰 역순 출력.
        lm.stackFromEnd = true    // 리사이클러뷰 역순 출력.
        PictureRecyclerView.layoutManager = lm

        super.onViewCreated(view, savedInstanceState)
    }
    // 사용자의 이메일로 MySQL 에 저장된 DB를 확인한 후, 불러와서 사용자에게 리스트를 보여준다.
    private fun ImageList(email: String?) {
        compositeDisposable.add(myAPI.ImageList(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Image ->
                PictureRecyclerView.adapter = PictureRecyclerAdapter(context!!, Image)
                (PictureRecyclerView.adapter as PictureRecyclerAdapter).notifyDataSetChanged()
            }
                , { thr ->
                    Log.d("PictureRecyclerView", thr.message.toString())
                }

            ))
    }

    override fun onStart() {    // 사진을 업로드하고 나왔을때 업로드한 사진이 리스트 맨위에서 볼수 있도록 한다.
        ImageList(Common.UserInfomation?.email)
        super.onStart()
    }
}
