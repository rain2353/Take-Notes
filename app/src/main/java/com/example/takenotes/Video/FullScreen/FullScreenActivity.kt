package com.example.takenotes.Video.FullScreen

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.takenotes.R
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_full_screen.*


class FullScreenActivity : AppCompatActivity() { 


    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        playerView.setPlayer(player)

        val intent = intent
        val videoUrl = intent.getStringExtra("videoUrl")
        val currentPos = intent.getLongExtra("currentPos", 0)


        //미디어 소스 객체 생성
        val factory: DataSource.Factory = DefaultDataSourceFactory(this, "Ex90Exoplayer")
        val mediaFactory = ProgressiveMediaSource.Factory(factory)

        val mediaSource =
            mediaFactory.createMediaSource(Uri.parse(videoUrl))
        player!!.prepare(mediaSource)
        player!!.setPlayWhenReady(true)
        // 사용자가 전체화면 버튼을 누른 시점부터 재생한다.
        player!!.seekTo(currentPos)
    }
}
