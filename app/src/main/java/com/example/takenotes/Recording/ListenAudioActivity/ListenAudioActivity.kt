package com.example.takenotes.Recording.ListenAudioActivity

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
import com.example.takenotes.Common.Common.SelectAudio
import com.example.takenotes.R
import com.example.takenotes.Utils.AudioRequestBody
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
import kotlinx.android.synthetic.main.activity_listen_audio.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListenAudioActivity : AppCompatActivity(), AudioRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) { 
        dialog.progress = percentage
    }

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    val REQUEST_TAKE_ALBUM = 1
    private var SelectedAudio: Uri? = null
    private var ExoplayerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listen_audio)

        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        ExoplayerView = findViewById(R.id.ExoplayerView)

        // 사용자가 클릭한 아이템의 동영상 Uri
        SelectedAudio = Uri.parse(ipaddress.ip + Common.SelectAudio?.audio)
        // 사용자가 작성한 제목과 내용
        editTitle.setText(Common.SelectAudio?.title)
        editContent.setText(Common.SelectAudio?.content)
        // 사용자가 동영상을 업로드한 시간과 수정한 시간을 보여준다.
        textUpdated.text = "마지막 수정: " + Common.SelectAudio?.updated_at
        textCreated.text = "만든 날짜: " + Common.SelectAudio?.created_at


    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(ListenAudioToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "음성녹음 듣기"
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
                DeleteAudio(Common.SelectAudio?.num)
            }
            R.id.select -> {      // 사용자 갤러리로 이동해서 동영상을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*"
                startActivityForResult(intent, REQUEST_TAKE_ALBUM)

            }
            R.id.upload_save -> {     // 글 작성 완료 버튼
                if (SelectedAudio == Uri.parse(ipaddress.ip + Common.SelectAudio?.audio)) {    // 녹음 파일은 변경되지 않고 텍스트만 변경이 되는 경우.
                    AudioTextModify(
                        Common.SelectAudio?.num,
                        Common.SelectAudio?.email,
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectAudio?.audio,
                        Common.SelectAudio?.created_at
                    )
                } else {   // 녹음 파일이 변경된 경우.
                    // 사용자에게 동영상이 업로드 되고있는 상황을 보여준다.
                    dialog = ProgressDialog(this)
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    dialog.setMessage("오디오 파일을 업로드중입니다...")
                    dialog.isIndeterminate = false
                    dialog.setCancelable(false)
                    dialog.max = 100
                    dialog.show()


                    val audio = prepareFilePart("audio", SelectedAudio)
                    myAPI.AudioModify(
                        Common.SelectAudio?.num,
                        audio,
                        Common.UserInfomation?.email.toString(),
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectAudio?.created_at
                    )
                        .enqueue(object : Callback<String> {

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("t", t.message.toString())
                                Toast.makeText(
                                    this@ListenAudioActivity,
                                    t.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Toast.makeText(
                                    this@ListenAudioActivity,
                                    "수정하였습니다.",
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

    @NonNull
    private fun prepareFilePart(partName: String, fileUri: Uri?): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = AudioRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_ALBUM) {
                if (data != null) {
                    SelectedAudio = data.data
                    if (SelectedAudio != null) {
                        initializePlayer()
                    }
                }

            }
        }
    }

    // 음성녹음 삭제하기.
    fun DeleteAudio(num: Int?) {
        compositeDisposable.add(myAPI.DeleteAudio(num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Toast.makeText(this, "음성녹음 파일을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                finish()

            }
                , { thr ->
                    Toast.makeText(this, "음성녹음 파일을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()

                }

            ))
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

        val mediaSource: MediaSource = this.buildMediaSource(SelectedAudio!!)!!
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

    // 서버와 통신하여 MySQL에 지정된 Table 에 수정하여 저장한다.
    private fun AudioTextModify(
        num: Int?,
        email: String?,
        title: String?,
        content: String?,
        audio: String?,
        created_at: String?
    ) {
        compositeDisposable.add(myAPI.AudioTextModify(num, email, title, content, audio, created_at)
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
