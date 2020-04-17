package com.example.takenotes.Picture.SeePictureActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Api.ipaddress
import com.example.takenotes.Common.Common
import com.example.takenotes.PictureViewPager.PictureViewPagerActivity
import com.example.takenotes.R
import com.example.takenotes.Utils.ProgressRequestBody
import com.ipaulpro.afilechooser.utils.FileUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_see_picture.*
import kotlinx.android.synthetic.main.activity_see_picture.editContent
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeePictureActivity : AppCompatActivity(), ProgressRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int) {
        dialog.progress = percentage
    }

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    lateinit var dialog: ProgressDialog
    val REQUEST_TAKE_ALBUM = 1
    var imageList = ArrayList<Uri>()
    var photouri: Uri? = null
    var images = ArrayList<MultipartBody.Part>()
    var num: Int? = null   // 뷰페이저에서 생성 할 Fragment 의 개수

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_picture)
        setToolbar()

        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        // 사용자가 제목을 입력하고 사진 업로드를 하였을때는 입력한 제목이 나온다.
        if (Common.SelectPicture?.title != null) {
            editTitle.setText(Common.SelectPicture?.title)
        } else { // 그렇지 않으면 hint를 보이게 하여 제목을 다시 작성할수 있게끔 한다.
            editTitle.hint = "제목을 입력해주세요."
        }

        // 사용자가 내용을 입력하고 사진 업로드를 했을때 입력했던 내용을 사용자가 볼수있게 한다.
        if (Common.SelectPicture?.content != null) {
            editContent.setText(Common.SelectPicture?.content)
        } else {  // 그렇지 않으면 내용을 입력할수 있게 한다.
            editContent.hint = "내용을 입력해주세요."
        }

        //사용자가 업로드한 사진들을 볼수 있게 한다.
        if (Common.SelectPicture?.file != "empty") {  // DB에 저장된 값이 "empty" 이면 ImageView 를 사용자에게 보이지 않게한다.
            SeePicture.visibility =
                View.VISIBLE     // DB에 저장된 값이 "empty" 가 아니면 ImageView 를 보이게 하고 Glide Library 를 사용하여 사용자에게 업로드한 사진을 보여준다.
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file).override(700, 700)
                .into(SeePicture)
            photouri = Uri.parse(ipaddress.ip + Common.SelectPicture?.file)
            num = 1   // 뷰페이저에서 생성 할 Fragment 의 개수
        } else {
            SeePicture.visibility = View.GONE
            num = 0   // 뷰페이저에서 생성 할 Fragment 의 개수
        }
        if (Common.SelectPicture?.file1 != "empty") {
            SeePicture1.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file1).override(700, 700)
                .into(SeePicture1)
            num = 2
        } else {
            SeePicture1.visibility = View.GONE 

        }
        if (Common.SelectPicture?.file2 != "empty") {
            SeePicture2.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file2).override(700, 700)
                .into(SeePicture2)
            num = 3
        } else {
            SeePicture2.visibility = View.GONE

        }
        if (Common.SelectPicture?.file3 != "empty") {
            SeePicture3.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file3).override(700, 700)
                .into(SeePicture3)
            num = 4
        } else {
            SeePicture3.visibility = View.GONE

        }
        if (Common.SelectPicture?.file4 != "empty") {
            SeePicture4.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file4).override(700, 700)
                .into(SeePicture4)
            num = 5
        } else {
            SeePicture4.visibility = View.GONE

        }
        if (Common.SelectPicture?.file5 != "empty") {
            SeePicture5.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file5).override(700, 700)
                .into(SeePicture5)
            num = 6
        } else {
            SeePicture5.visibility = View.GONE

        }
        if (Common.SelectPicture?.file6 != "empty") {
            SeePicture6.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file6).override(700, 700)
                .into(SeePicture6)
            num = 7
        } else {
            SeePicture6.visibility = View.GONE

        }
        if (Common.SelectPicture?.file7 != "empty") {
            SeePicture7.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file7).override(700, 700)
                .into(SeePicture7)
            num = 8
        } else {
            SeePicture7.visibility = View.GONE

        }
        if (Common.SelectPicture?.file8 != "empty") {
            SeePicture8.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file8).override(700, 700)
                .into(SeePicture8)
            num = 9
        } else {
            SeePicture8.visibility = View.GONE

        }
        if (Common.SelectPicture?.file9 != "empty") {
            SeePicture9.visibility = View.VISIBLE
            Glide.with(this).load(ipaddress.ip + Common.SelectPicture?.file9).override(700, 700)
                .into(SeePicture9)
            num = 10
        } else {
            SeePicture9.visibility = View.GONE

        }

        // 사용자가 사진을 업로드한 시간과 수정한 시간을 보여준다.
        textUpdated.text = "마지막 수정: " + Common.SelectPicture?.updated_at
        textCreated.text = "만든 날짜: " + Common.SelectPicture?.created_at


        // 사용자가 사진을 좀 더 자세하게 볼수 있게 하기위해서 뷰페이저를 사용하였다.
        // 사용자가 사진을 클릭하게되면 뷰페이저로 이동한다.
        SeePicture.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)  // 뷰페이저에서 생성 할 Fragment 의 개수
            startActivity(intent)
        }
        SeePicture1.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture2.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture3.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture4.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture5.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture6.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture7.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture8.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
        SeePicture9.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum", num!!)
            startActivity(intent)
        }
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(SeePictureToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "사진 보기"
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
            R.id.delete -> {       // 삭제 버튼 누르면 게시글이 삭제 된다.
                DeletePicture(Common.SelectPicture?.num)
            }
            R.id.select -> {      // 사용자 갤러리로 이동해서 사진을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {   // 이미지를 다중선택 할수있는 안드로이드 버전만 다중선택 가능.
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(intent, REQUEST_TAKE_ALBUM)
                } else {   // 그렇지 않으면 사진 한장만 선택.
                    startActivityForResult(intent, REQUEST_TAKE_ALBUM)
                }
            }
            R.id.upload_save -> {     // 글 작성 완료 버튼

                if (photouri == Uri.parse(ipaddress.ip + Common.SelectPicture?.file)) { // 사진은 변경하지 않고 텍스트만 변경하는 경우.
                    ImageTextModify(
                        Common.SelectPicture?.num,
                        Common.SelectPicture?.email,
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectPicture?.file,
                        Common.SelectPicture?.file1,
                        Common.SelectPicture?.file2,
                        Common.SelectPicture?.file3,
                        Common.SelectPicture?.file4,
                        Common.SelectPicture?.file5,
                        Common.SelectPicture?.file6,
                        Common.SelectPicture?.file7,
                        Common.SelectPicture?.file8,
                        Common.SelectPicture?.file9,
                        Common.SelectPicture?.created_at
                    )
                } else { // 사진을 변경하는 경우.
                    images.clear()
                    for (i in 0 until imageList.size) {
                        images.add(prepareFilePart("files", imageList.get(i)))
                        Log.d("images", images.toString())
                    }

                    // 사용자에게 사진이 업로드 되고있는 상황을 보여준다.
                    dialog = ProgressDialog(this)
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    dialog.setMessage("사진을 업로드중입니다...")
                    dialog.isIndeterminate = false
                    dialog.setCancelable(false)
                    dialog.max = 100
                    dialog.show()
                    myAPI.ImageModify(
                        Common.SelectPicture?.num,
                        images,
                        Common.UserInfomation?.email.toString(),
                        editTitle.text.toString(),
                        editContent.text.toString(),
                        Common.SelectPicture?.created_at
                    )
                        .enqueue(object : Callback<String> {

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("t", t.message.toString())
                                Toast.makeText(
                                    this@SeePictureActivity,
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
                                    this@SeePictureActivity,
                                    "사진을 수정하였습니다.",
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
        return super.onOptionsItemSelected(item)
    }

    @NonNull
    private fun prepareFilePart(partName: kotlin.String, fileUri: Uri): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = ProgressRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    // 사진 삭제하기.
    fun DeletePicture(num: Int?) {
        compositeDisposable.add(myAPI.DeletePicture(num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Toast.makeText(this, "업로드한 사진을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
                , { thr ->
                    Toast.makeText(this, "사진을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

            ))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_ALBUM -> {
                Log.i("result", java.lang.String.valueOf(resultCode))
                if (resultCode === Activity.RESULT_OK) {
                    imageList.clear()
                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                    if (data?.clipData == null) {
                        Log.i(
                            "1. single choice",
                            java.lang.String.valueOf(data?.data)
                        )
                        imageList.add(data?.data!!)
                        Glide.with(this).load(data.data).override(700, 700).into(SeePicture)
                        // 한장만 지원이 될 경우 다른 이미지뷰들은 보이지 않게 한다.
                        SeePicture.visibility = View.VISIBLE
                        SeePicture1.visibility = View.GONE
                        SeePicture2.visibility = View.GONE
                        SeePicture3.visibility = View.GONE
                        SeePicture4.visibility = View.GONE
                        SeePicture5.visibility = View.GONE
                        SeePicture6.visibility = View.GONE
                        SeePicture7.visibility = View.GONE
                        SeePicture8.visibility = View.GONE
                        SeePicture9.visibility = View.GONE
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
                            Glide.with(this).load(clipData.getItemAt(0).uri).override(700, 700)
                                .into(SeePicture)
                            SeePicture.visibility = View.VISIBLE
                            SeePicture1.visibility = View.GONE
                            SeePicture2.visibility = View.GONE
                            SeePicture3.visibility = View.GONE
                            SeePicture4.visibility = View.GONE
                            SeePicture5.visibility = View.GONE
                            SeePicture6.visibility = View.GONE
                            SeePicture7.visibility = View.GONE
                            SeePicture8.visibility = View.GONE
                            SeePicture9.visibility = View.GONE
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
                            SeePicture.visibility = View.GONE
                            SeePicture1.visibility = View.GONE
                            SeePicture2.visibility = View.GONE
                            SeePicture3.visibility = View.GONE
                            SeePicture4.visibility = View.GONE
                            SeePicture5.visibility = View.GONE
                            SeePicture6.visibility = View.GONE
                            SeePicture7.visibility = View.GONE
                            SeePicture8.visibility = View.GONE
                            SeePicture9.visibility = View.GONE
                            while (i < clipData.itemCount) {
                                Log.i(
                                    "3. single choice",
                                    clipData.getItemAt(i).uri.toString()
                                )

                                when (i) {
                                    0 -> {
                                        Glide.with(this).load(clipData.getItemAt(0).uri)
                                            .override(700, 700).into(SeePicture)
                                        SeePicture.visibility = View.VISIBLE
                                    }
                                    1 -> {
                                        Glide.with(this).load(clipData.getItemAt(1).uri)
                                            .override(700, 700).into(SeePicture1)
                                        SeePicture1.visibility = View.VISIBLE
                                    }
                                    2 -> {
                                        Glide.with(this).load(clipData.getItemAt(2).uri)
                                            .override(700, 700).into(SeePicture2)
                                        SeePicture2.visibility = View.VISIBLE
                                    }
                                    3 -> {
                                        Glide.with(this).load(clipData.getItemAt(3).uri)
                                            .override(700, 700).into(SeePicture3)
                                        SeePicture3.visibility = View.VISIBLE
                                    }
                                    4 -> {
                                        Glide.with(this).load(clipData.getItemAt(4).uri)
                                            .override(700, 700).into(SeePicture4)
                                        SeePicture4.visibility = View.VISIBLE

                                    }
                                    5 -> {
                                        Glide.with(this).load(clipData.getItemAt(5).uri)
                                            .override(700, 700).into(SeePicture5)
                                        SeePicture5.visibility = View.VISIBLE
                                    }
                                    6 -> {
                                        Glide.with(this).load(clipData.getItemAt(6).uri)
                                            .override(700, 700).into(SeePicture6)
                                        SeePicture6.visibility = View.VISIBLE
                                    }
                                    7 -> {
                                        Glide.with(this).load(clipData.getItemAt(7).uri)
                                            .override(700, 700).into(SeePicture7)
                                        SeePicture7.visibility = View.VISIBLE
                                    }
                                    8 -> {
                                        Glide.with(this).load(clipData.getItemAt(8).uri)
                                            .override(700, 700).into(SeePicture8)
                                        SeePicture8.visibility = View.VISIBLE
                                    }
                                    9 -> {
                                        Glide.with(this).load(clipData.getItemAt(9).uri)
                                            .override(700, 700).into(SeePicture9)
                                        SeePicture9.visibility = View.VISIBLE
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

    // 서버와 통신하여 MySQL에 지정된 Table 에 수정하여 저장한다.
    private fun ImageTextModify(
        num: Int?,
        email: String?,
        title: String?,
        content: String?,
        file: String?,
        file1: String?,
        file2: String?,
        file3: String?,
        file4: String?,
        file5: String?,
        file6: String?,
        file7: String?,
        file8: String?,
        file9: String?,
        created_at: String?
    ) {
        compositeDisposable.add(myAPI.ImageTextModify(
            num,
            email,
            title,
            content,
            file,
            file1,
            file2,
            file3,
            file4,
            file5,
            file6,
            file7,
            file8,
            file9,
            created_at
        )
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
