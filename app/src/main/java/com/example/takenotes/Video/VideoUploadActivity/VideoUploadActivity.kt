package com.example.takenotes.Video.VideoUploadActivity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.example.takenotes.Utils.VideoRequestBody
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.ipaulpro.afilechooser.utils.FileUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_video_upload.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class VideoUploadActivity : AppCompatActivity(), VideoRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        dialog.progress = percentage
    }
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    val REQUEST_TAKE_ALBUM = 1
    private var selectedvideo: Uri? = null
    private var videopath: String? = null
    private var ExoplayerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_upload)
        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        ExoplayerView = findViewById(R.id.ExoplayerView)
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(VideoUploadToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "동영상 올리기"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(
            R.menu.image_upload_menu,
            menu
        )       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    // 툴바 버튼 설정
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            android.R.id.home -> {        // 뒤로가기 버튼
                finish()
                return true
            }
            R.id.select_image -> {      // 사용자 갤러리로 이동해서 동영상을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "video/*"
                startActivityForResult(intent, REQUEST_TAKE_ALBUM)

            }
            R.id.image_upload_save -> {     // 글 작성 완료 버튼
                // 사용자에게 동영상이 업로드 되고있는 상황을 보여준다.
                dialog = ProgressDialog(this)
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                dialog.setMessage("동영상을 업로드중입니다...")
                dialog.isIndeterminate = false
                dialog.setCancelable(false)
                dialog.max = 100
                dialog.show()


                val video = prepareFilePart("video", selectedvideo)
                myAPI.VideoUpload(video, Common.UserInfomation?.email.toString(), editTitle.text.toString(), editContent.text.toString())
                    .enqueue(object : Callback<String> {

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("t", t.message.toString())
                            Toast.makeText(this@VideoUploadActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>
                        ) {
                            Toast.makeText(
                                this@VideoUploadActivity,
                                "선택하신 동영상을 업로드하였습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            dialog.dismiss()
                            finish()
                        }

                    })
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_ALBUM) {
                if (data != null) {
                    selectedvideo = data.data
                    videopath = data.data!!.path
                    Log.d("path_uri",selectedvideo.toString())
                    Log.d("path",videopath)
                    if (selectedvideo != null) {
                        initializePlayer()
                    }
                }

            }
        }
    }


    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this.applicationContext)
            //플레이어 연결
            ExoplayerView?.player = player
        }

        val mediaSource: MediaSource = this.buildMediaSource(selectedvideo!!)!!
        //prepare
        player!!.prepare(mediaSource, true, false)
        //start,stop
        player!!.playWhenReady = playWhenReady
    }
    private fun buildMediaSource(uri: Uri): MediaSource? {
        val userAgent: String = Util.getUserAgent(this, "Take Notes")
        return if (uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")) {
            ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else if (uri.lastPathSegment!!.contains("m3u8")) { //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, userAgent))
                .createMediaSource(uri)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            ExoplayerView?.player = null
            player!!.release()
            player = null
        }
    }

    @NonNull
    private fun prepareFilePart(partName: String, fileUri: Uri?): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = VideoRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

}
