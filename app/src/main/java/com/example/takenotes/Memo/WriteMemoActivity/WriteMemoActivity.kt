package com.example.takenotes.Memo.WriteMemoActivity

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_write_memo.*

class WriteMemoActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_memo)
        setToolbar()

        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(WriteMemoToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "메모 작성"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.write_memo_save, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
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
            R.id.write_memo_save -> {     // 글 작성 완료 버튼
                if (editMemoTitle.text.isEmpty()) {
                    editMemoTitle.requestFocus()
                    Toast.makeText(this, "메모 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return true
                } else if (editMemoContent.text.isEmpty()) {
                    editMemoContent.requestFocus()
                    Toast.makeText(this, "메모 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return true
                } else {
                    WriteMemoUpload(
                        Common.UserInfomation?.email,
                        editMemoTitle.text.toString(),
                        editMemoContent.text.toString()
                    )
                }
                Log.d("memo_title", editMemoTitle.text.toString())
                Log.d("memo_content", editMemoContent.text.toString())
                Log.d("memo_writer", Common.UserInfomation?.email.toString())
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    // 서버와 통신하여 MySQL에 지정된 Table 에 저장한다.
    private fun WriteMemoUpload(email: String?, title: String?, content: String?) {
        compositeDisposable.add(myAPI.WriteMemoUpload(email, title, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.contains("success")) {
                    Toast.makeText(this, "메모 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            })
    }


}
