package com.example.takenotes.Memo.ReadMemoActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_read_memo.*

class ReadMemoActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_memo)
        setToolbar()

        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        editTitle.setText(Common.SelectMemo?.title)
        editContent.setText(Common.SelectMemo?.content)
        textUpdated.text = "마지막 수정: " + Common.SelectMemo?.updated_at
        textCreated.text = "만든 날짜: " + Common.SelectMemo?.created_at
    }
    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(ReadMemoToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "메모 작성"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.modify_memo, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
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
            R.id.delete -> {   // 글 삭제 버튼.
                DeleteMemo(Common.SelectMemo?.num)
            }
            R.id.save -> {     // 글 작성 완료 버튼
                when {
                    editTitle.text.isEmpty() -> {
                        editTitle.requestFocus()
                        Toast.makeText(this, "메모 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    editContent.text.isEmpty() -> {
                        editContent.requestFocus()
                        Toast.makeText(this, "메모 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    else -> {
                        ChangeMemoUpload(
                            Common.SelectMemo?.num,
                            Common.UserInfomation?.email,
                            editTitle.text.toString(),
                            editContent.text.toString(),
                            Common.SelectMemo?.created_at
                        )
                    }
                }
                Log.d("memo_title", editTitle.text.toString())
                Log.d("memo_content", editContent.text.toString())
                Log.d("memo_writer", Common.UserInfomation?.email.toString())
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
    // 서버와 통신하여 MySQL에 지정된 Table 에 저장한다.
    private fun ChangeMemoUpload(num: Int?,email: String?, title: String?, content: String?, created_at: String?) {
        compositeDisposable.add(myAPI.ChangeMemoUpload(num,email, title, content,created_at)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.contains("success")) {
                    Toast.makeText(this, "메모를 수정하였습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 메모 삭제하기.
    fun DeleteMemo(num: Int?) {
        compositeDisposable.add(myAPI.DeleteMemo(num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ message ->
                Toast.makeText(this, "메모를 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                finish()

            }
                , { thr ->
                    Toast.makeText(this, "메모를 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show()

                }

            ))
    }
}
