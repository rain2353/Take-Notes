package com.example.takenotes.Picture.ImageUploadActivity


import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.example.takenotes.Utils.ProgressRequestBody
import com.ipaulpro.afilechooser.utils.FileUtils

import io.reactivex.disposables.CompositeDisposable

import kotlinx.android.synthetic.main.activity_image_upload.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.lang.String


class ImageUploadActivity : AppCompatActivity(), ProgressRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        dialog.progress = percentage
    }
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    val REQUEST_TAKE_ALBUM = 1
    var imageList = ArrayList<Uri>()
    var uri: Uri? = null
    var images = ArrayList<MultipartBody.Part>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)
        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        //선택한 이미지 클릭했을때 이미지를 크게 해서 사용자가 확인 할수 있게 한다.
        SelectImageView0.setOnClickListener {
            uri = Uri.parse(imageList[0].toString())
            ImageCheck.requestFocus()
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView1.setOnClickListener {
            uri = Uri.parse(imageList[1].toString())
            ImageCheck.requestFocus()
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView2.setOnClickListener {
            uri = Uri.parse(imageList[2].toString())
            ImageCheck.requestFocus()
            ImageCheck.visibility = View.VISIBLE
            ImageCheck.setImageURI(uri)
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView3.setOnClickListener {
            uri = Uri.parse(imageList[3].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView4.setOnClickListener {
            uri = Uri.parse(imageList[4].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView5.setOnClickListener {
            uri = Uri.parse(imageList[5].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck) 
        }
        SelectImageView6.setOnClickListener {
            uri = Uri.parse(imageList[6].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView7.setOnClickListener {
            uri = Uri.parse(imageList[7].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView8.setOnClickListener {
            uri = Uri.parse(imageList[8].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
        SelectImageView9.setOnClickListener {
            uri = Uri.parse(imageList[9].toString())
            ImageCheck.visibility = View.VISIBLE
            Glide.with(this).load(uri).override(900,900).into(ImageCheck)
        }
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(ImageUploadToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "사진 올리기"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(
            R.menu.upload_menu,
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
            R.id.select_image -> {      // 사용자 갤러리로 이동해서 사진을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {   // 이미지를 다중선택 할수있는 안드로이드 버전만 다중선택 가능.
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(intent, REQUEST_TAKE_ALBUM)
                } else {   // 그렇지 않으면 사진 한장만 선택.
                    startActivityForResult(intent, REQUEST_TAKE_ALBUM)
                }
            }
            R.id.image_upload_save -> {     // 글 작성 완료 버튼
                images.clear()

                for (i in 0 until imageList.size) {
                    images.add(prepareFilePart("files", imageList.get(i)))
                    Log.d("images",images.toString())
                }

                // 사용자에게 사진이 업로드 되고있는 상황을 보여준다.
                dialog = ProgressDialog(this)
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                dialog.setMessage("사진을 업로드중입니다...")
                dialog.isIndeterminate = false
                dialog.setCancelable(false)
                dialog.max = 100
                dialog.show()
                myAPI.ImageUpload(images, Common.UserInfomation?.email.toString(), editTextTitle.text.toString(), editContent.text.toString())
                    .enqueue(object : Callback<kotlin.String> {

                        override fun onFailure(call: Call<kotlin.String>, t: Throwable) {
                            Log.d("t", t.message.toString())
                            Toast.makeText(this@ImageUploadActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<kotlin.String>,
                            response: Response<kotlin.String>
                        ) {
                            Toast.makeText(
                                this@ImageUploadActivity,
                                "선택하신 사진을 업로드하였습니다.",
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_ALBUM -> {
                Log.i("result", String.valueOf(resultCode))
                if (resultCode === Activity.RESULT_OK) {
                    imageList.clear()
                    ImageCheck.setImageResource(0)
                    SelectImageView0.setImageResource(0)
                    SelectImageView0.visibility = View.GONE
                    SelectImageView1.setImageResource(0)
                    SelectImageView1.visibility = View.GONE
                    SelectImageView2.setImageResource(0)
                    SelectImageView2.visibility = View.GONE
                    SelectImageView3.setImageResource(0)
                    SelectImageView3.visibility = View.GONE
                    SelectImageView4.setImageResource(0)
                    SelectImageView4.visibility = View.GONE
                    SelectImageView5.setImageResource(0)
                    SelectImageView5.visibility = View.GONE
                    SelectImageView6.setImageResource(0)
                    SelectImageView6.visibility = View.GONE
                    SelectImageView7.setImageResource(0)
                    SelectImageView7.visibility = View.GONE
                    SelectImageView8.setImageResource(0)
                    SelectImageView8.visibility = View.GONE
                    SelectImageView9.setImageResource(0)
                    SelectImageView9.visibility = View.GONE
                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                    if (data?.clipData == null) {
                        Log.i(
                            "1. single choice",
                            String.valueOf(data?.data)
                        )
                        imageList.add(data?.data!!)
                        Glide.with(this).load(data?.data).override(300,300).into(SelectImageView0)
                        SelectImageView0.visibility = View.VISIBLE
                    } else {
                        val clipData: ClipData = data.clipData!!
                        Log.i("clipdata", clipData.itemCount.toString())
                        if (clipData.itemCount > 10) {
                            Toast.makeText(
                                this,
                                "사진은 10개까지 선택가능 합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        } else if (clipData.itemCount == 1) {
                            val dataStr = clipData.getItemAt(0).uri
                            SelectImageView0.setImageURI(clipData.getItemAt(0).uri)
                            SelectImageView0.visibility = View.VISIBLE
                            Log.i(
                                "2. clipdata choice",
                                clipData.getItemAt(0).uri.toString()
                            )
                            Log.i(
                                "2. single choice",
                                clipData.getItemAt(0).uri.path
                            )
                            imageList.add(dataStr)
                        } else if (clipData.itemCount in 2..10) {
                            var i = 0
                            while (i < clipData.itemCount) {
                                Log.i(
                                    "3. single choice",
                                    clipData.getItemAt(i).uri.toString()
                                )

                                when (i) {
                                    0 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView0)
                                        SelectImageView0.visibility = View.VISIBLE
                                    }
                                    1 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView1)
                                        SelectImageView1.visibility = View.VISIBLE
                                    }
                                    2 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView2)
                                        SelectImageView2.visibility = View.VISIBLE
                                    }
                                    3 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView3)
                                        SelectImageView3.visibility = View.VISIBLE
                                    }
                                    4 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView4)
                                        SelectImageView4.visibility = View.VISIBLE

                                    }
                                    5 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView5)
                                        SelectImageView5.visibility = View.VISIBLE
                                    }
                                    6 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView6)
                                        SelectImageView6.visibility = View.VISIBLE
                                    }
                                    7 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView7)
                                        SelectImageView7.visibility = View.VISIBLE
                                    }
                                    8 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView8)
                                        SelectImageView8.visibility = View.VISIBLE
                                    }
                                    9 -> {
                                        Glide.with(this).load(clipData.getItemAt(i).uri).override(300,300).into(SelectImageView9)
                                        SelectImageView9.visibility = View.VISIBLE
                                    }
                                }
                                imageList.add(clipData.getItemAt(i).uri)
                                i++

                            }
                            Log.d("SelectImage", imageList.toString())

                        }
                    }

                } else {
                    Toast.makeText(this, "사진 선택을 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @NonNull
    private fun prepareFilePart(partName: kotlin.String, fileUri: Uri): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = ProgressRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

}



