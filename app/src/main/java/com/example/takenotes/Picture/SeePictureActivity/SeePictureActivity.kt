package com.example.takenotes.Picture.SeePictureActivity

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import com.example.takenotes.Common.Common
import com.example.takenotes.PictureViewPager.PictureViewPagerActivity
import com.example.takenotes.R
import com.example.takenotes.Utils.ProgressRequestBody
import com.ipaulpro.afilechooser.utils.FileUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_see_picture.*
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
    var uri: Uri? = null
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
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file).override(700,700).into(SeePicture)
            num = 1   // 뷰페이저에서 생성 할 Fragment 의 개수
        } else {
            SeePicture.visibility = View.GONE
            num = 0   // 뷰페이저에서 생성 할 Fragment 의 개수
        }
        if (Common.SelectPicture?.file1 != "empty") {
            SeePicture1.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file1).override(700,700).into(SeePicture1)
            num = 2
        } else {
            SeePicture1.visibility = View.GONE

        }
        if (Common.SelectPicture?.file2 != "empty") {
            SeePicture2.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file2).override(700,700).into(SeePicture2)
            num = 3
        } else {
            SeePicture2.visibility = View.GONE

        }
        if (Common.SelectPicture?.file3 != "empty") {
            SeePicture3.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file3).override(700,700).into(SeePicture3)
            num = 4
        } else {
            SeePicture3.visibility = View.GONE

        }
        if (Common.SelectPicture?.file4 != "empty") {
            SeePicture4.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file4).override(700,700).into(SeePicture4)
            num = 5
        } else {
            SeePicture4.visibility = View.GONE

        }
        if (Common.SelectPicture?.file5 != "empty") {
            SeePicture5.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file5).override(700,700).into(SeePicture5)
            num = 6
        } else {
            SeePicture5.visibility = View.GONE

        }
        if (Common.SelectPicture?.file6 != "empty") {
            SeePicture6.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file6).override(700,700).into(SeePicture6)
            num = 7
        } else {
            SeePicture6.visibility = View.GONE

        }
        if (Common.SelectPicture?.file7 != "empty") {
            SeePicture7.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file7).override(700,700).into(SeePicture7)
            num = 8
        } else {
            SeePicture7.visibility = View.GONE

        }
        if (Common.SelectPicture?.file8 != "empty") {
            SeePicture8.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file8).override(700,700).into(SeePicture8)
            num = 9
        } else {
            SeePicture8.visibility = View.GONE

        }
        if (Common.SelectPicture?.file9 != "empty") {
            SeePicture9.visibility = View.VISIBLE
            Glide.with(this).load("http://10.0.2.2:3000/" + Common.SelectPicture?.file9).override(700,700).into(SeePicture9)
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
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture2.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture3.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture4.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture5.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture6.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture7.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture8.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
        SeePicture9.setOnClickListener {
            val intent = Intent(this, PictureViewPagerActivity::class.java)
            intent.putExtra("PagerNum",num!!)
            startActivity(intent)
        }
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(SeePictureToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "업로드한 사진 보기"
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
            R.id.select_image -> {      // 사용자 갤러리로 이동해서 사진을 선택할수있는 버튼.
                val intent = Intent(Intent.ACTION_PICK)
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
                myAPI.ImageUpload(
                    images,
                    Common.UserInfomation?.email.toString(),
                    editTitle.text.toString(),
                    editContent.text.toString()
                )
                    .enqueue(object : Callback<String> {

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("t", t.message.toString())
                            Toast.makeText(this@SeePictureActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>
                        ) {
                            Toast.makeText(
                                this@SeePictureActivity,
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

    @NonNull
    private fun prepareFilePart(partName: kotlin.String, fileUri: Uri): MultipartBody.Part {
        val file = FileUtils.getFile(this, fileUri)
        val requestFile = ProgressRequestBody(file, this)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
