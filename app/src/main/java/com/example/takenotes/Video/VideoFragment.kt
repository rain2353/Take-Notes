package com.example.takenotes.Video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.takenotes.R
import com.example.takenotes.Video.VideoUploadActivity.VideoUploadActivity
import kotlinx.android.synthetic.main.fragment_video.*


class VideoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 화면에 보이는 플러스 모양의 FloatingActionButton 을 누르면 동영상을 업로드할수 있는 액티비티로 이동한다.
        this.VideoUploadButton.setOnClickListener {
            val intent = Intent(context, VideoUploadActivity::class.java)
            startActivity(intent)
        }


        super.onViewCreated(view, savedInstanceState)
    }

}
