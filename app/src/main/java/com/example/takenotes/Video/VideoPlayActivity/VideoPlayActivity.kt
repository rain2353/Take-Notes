package com.example.takenotes.Video.VideoPlayActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Api.ipaddress
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.example.takenotes.Utils.VideoRequestBody
import com.example.takenotes.Video.FullScreen.FullScreenActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ipaulpro.afilechooser.utils.FileUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video_play.*
import kotlinx.android.synthetic.main.custom_controls.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoPlayActivity : AppCompatActivity(), VideoRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        dialog.progress = percentage
    }
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    val REQUEST_TAKE_ALBUM = 1
    private var selectedvideo: Uri? = null
    private var ExoplayerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    @SuppressLint("SetTextI18n") 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        ExoplayerView = findViewById(R.id.ExoplayerView)

        // 사용자가 클릭한 아이템의 동영상 Uri
        selectedvideo = Uri.parse(ipaddress.ip+Common.SelectVideo?.video)
        // 사용자가 작성한 제목과 내용
        editTitle.setText(Common.SelectVideo?.title)
        editContent.setText(Common.SelectVideo?.content)
        // 사용자가 동영상을 업로드한 시간과 수정한 시간을 보여준다.
        textUpdated.text = "마지막 수정: " + Common.SelectVideo?.updated_at
        textCreated.text = "만든 날짜: " + Common.SelectVideo?.created_at


        // 동영상 전체 화면.
        exo_fullscreen_button.setOnClickListener {
            var videoUrl = selectedvideo.toString()
            var currentPos = player?.currentPosition
            val intent = Intent(this,FullScreenActivity::class.java)
            intent.putExtra("videoUrl",videoUrl)
            intent.putExtra("currentPos",currentPos)
            startActivity(intent)
        }
    }
    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(VideoPlayToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "동영상 보기"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(
            R.menu.modify_menu,
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
            R.id.delete -> {      // 사용자가 삭제버튼을 누르면 게시글을 삭제한다.
                DeleteVideo(Common.SelectVideo?.num)
            }
            R.id.select -> {      // 사용자 갤러리로 이동해서 동영상을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "video/*"
                startActivityForResult(intent, REQUEST_TAKE_ALBUM)

            }
            R.id.upload_save -> {     // 글 작성 완료 버튼
                if (selectedvideo == Uri.parse(ipaddress.ip+Common.SelectVideo?.video)){   // 동영상이 변경되지 않고 텍스트만 변경되는 경우.
                    VideoTextModify(
                        Common.SelectVideo?.num,
                        Common.SelectVideo?.email,
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectVideo?.video,
                        Common.SelectVideo?.created_at
                    )
                }else{
                    // 사용자에게 동영상이 업로드 되고있는 상황을 보여준다.
                    dialog = ProgressDialog(this)
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    dialog.setMessage("동영상을 업로드중입니다...")
                    dialog.isIndeterminate = false
                    dialog.setCancelable(false)
                    dialog.max = 100
                    dialog.show()


                    val video = prepareFilePart("video", selectedvideo)
                    myAPI.VideoModify(
                        Common.SelectVideo?.num,
                        video,
                        Common.UserInfomation?.email.toString(),
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectVideo?.created_at
                    )
                        .enqueue(object : Callback<String> {

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("t", t.message.toString())
                                Toast.makeText(this@VideoPlayActivity, t.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Toast.makeText(
                                    this@VideoPlayActivity,
                                    "선택하신 동영상을 수정하였습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                dialog.dismiss()
                                finish()
                            }

                        })
                }

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
                    if (selectedvideo != null) {
                        initializePlayer()
                    }
                }

            }
        }
    }
    @NonNull
    private fun prepareFilePart(partName: String, fileUri: Uri?): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = VideoRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
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

    // 동영상 삭제하기.
    fun DeleteVideo(num: Int?) {
        compositeDisposable.add(myAPI.DeleteVideo(num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Toast.makeText(this, "업로드한 동영상을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                finish()

            }
                , { thr ->
                    Toast.makeText(this, "동영상을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()

                }

            ))
    }

    // 서버와 통신하여 MySQL에 지정된 Table 에 수정하여 저장한다.
    private fun VideoTextModify(num: Int?,email: String?, title: String?, content: String?,video: String?, created_at: String?) {
        compositeDisposable.add(myAPI.VideoTextModify(num,email, title, content, video, created_at)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.contains("success")) {
                    Toast.makeText(this, "수정하였습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
