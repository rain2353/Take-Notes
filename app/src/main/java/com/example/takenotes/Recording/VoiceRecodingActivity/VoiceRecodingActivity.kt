package com.example.takenotes.Recording.VoiceRecodingActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.example.takenotes.Utils.AudioRequestBody
import com.ipaulpro.afilechooser.utils.FileUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_voice_recoding.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class VoiceRecodingActivity : AppCompatActivity(), View.OnClickListener,
    AudioRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        dialog.progress = percentage
    }

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private val RECORD_AUDIO_REQUEST_CODE = 101
    private var isPlaying = false

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgBtRecord -> {
                prepareRecording()
                startRecording()
            }

            R.id.imgBtStop -> {
                prepareStop()
                stopRecording()
            }

            R.id.imgViewPlay -> {
                if (!isPlaying && fileName != null) {
                    isPlaying = true
                    startPlaying()
                } else {
                    isPlaying = false
                    stopPlaying()
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_recoding)
        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio()
        }

        imgBtRecord.setOnClickListener(this)
        imgBtStop.setOnClickListener(this)
        imgViewPlay.setOnClickListener(this)
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(VoiceRecodingToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "음성 녹음하기"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(
            R.menu.write_memo_save,
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

            R.id.save -> {     // 글 작성 완료 버튼
                // 사용자에게 녹음한 파일이 업로드 되고있는 상황을 보여준다.
                dialog = ProgressDialog(this)
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                dialog.setMessage("녹음한 파일을 업로드중입니다...")
                dialog.isIndeterminate = false
                dialog.setCancelable(false)
                dialog.max = 100
                dialog.show()
                val PathtoUri = Uri.fromFile(File(fileName.toString()))
                Log.d("fileNameUri",PathtoUri.toString())
                val audio = prepareFilePart("audio", PathtoUri)
                myAPI.VoiceUpload(
                    audio,
                    Common.UserInfomation?.email.toString(),
                    editTitle.text.toString(),
                    editContent.text.toString()
                )
                    .enqueue(object : Callback<String> {

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            com.google.android.exoplayer2.util.Log.d("t", t.message.toString())
                            Toast.makeText(
                                this@VoiceRecodingActivity,
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
                                this@VoiceRecodingActivity,
                                "녹음하신 파일을 업로드하였습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            dialog.dismiss()
                            finish()
                        }

                    })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.VISIBLE
        imgBtStop.visibility = View.GONE
        llPlay.visibility = View.VISIBLE
    }


    private fun prepareRecording() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgBtRecord.visibility = View.GONE
        imgBtStop.visibility = View.VISIBLE
        llPlay.visibility = View.GONE
    }

    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        //showing the play button
        imgViewPlay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
        chronometer.stop()
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = android.os.Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath + "/TakeNotes/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }

        fileName = root.absolutePath + "/TakeNotes/Audios/" + (System.currentTimeMillis()
            .toString() + ".mp3")
        Log.d("filename1", fileName)
        mRecorder!!.setOutputFile(fileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        lastProgress = 0
        seekBar.progress = 0
        stopPlaying()
        // making the imageView a stop button starting the chronometer
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }


    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
        //showing the play button
        Toast.makeText(this, "음성 녹음 완료하였습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(fileName)
            mPlayer!!.prepare()
            mPlayer!!.start()
            Log.d("filename2", fileName)
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }

        //making the imageView pause button
        imgViewPlay.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp)

        seekBar.progress = lastProgress
        mPlayer!!.seekTo(lastProgress)
        seekBar.max = mPlayer!!.duration
        seekBarUpdate()
        chronometer.start()

        mPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            imgViewPlay.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
            isPlaying = false
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            mPlayer!!.seekTo(0)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    mPlayer!!.seekTo(progress)
                    chronometer.base = SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }


    private fun getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), RECORD_AUDIO_REQUEST_CODE
                )
            }
        }
    }

    // Callback with the request from calling requestPermissions(...)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    this,
                    "You must give permissions to use this app. App is exiting.",
                    Toast.LENGTH_SHORT
                ).show()
                finishAffinity()
            }
        }
    }

    @NonNull
    private fun prepareFilePart(partName: String, fileUri: Uri?): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = AudioRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


}
