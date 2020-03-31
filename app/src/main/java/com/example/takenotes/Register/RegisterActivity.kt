package com.example.takenotes.Register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Login.LoginActivity
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setToolbar()
        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)


        // 이메일의 형식이 맞으면 에러 메세지가 나오지 않고 형식이 틀리다면 에러 메세지를 사용자에게 보여준다.
        editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
            override fun afterTextChanged(s: Editable) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    EmailTextInputLayout.error = "이메일 형식이 아닙니다."
                } else {
                    EmailTextInputLayout.error = null // null은 에러 메시지를 지워주는 기능
                }
            }
        })

        // 비밀번호의 형식이 맞으면 에러 메세지가 나오지 않고 형식이 틀리다면 에러 메세지를 사용자에게 보여준다.
        editPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
            override fun afterTextChanged(s: Editable) {
                val passMatches = Regex("^(?=.*[a-zA-Z0-9])(?=.*[!@#\$%^*+=-]).{8,20}$")
                if (!s.matches(passMatches)) {
                    PasswordTextInputLayout.error = "영 소, 대문자와 숫자,특별기호를 함께 사용 해주세요. 8~20 제한"
                } else {
                    PasswordTextInputLayout.error = null // null은 에러 메시지를 지워주는 기능
                }
            }
        })

        // 휴대전화 번호의 형식이 맞으면 에러 메세지가 나오지 않고 형식이 틀리다면 에러 메세지를 사용자에게 보여준다.
        editPhone_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
            override fun afterTextChanged(s: Editable) {
                val phoneMatches = Regex("^01[016789][0-9]{3,4}[0-9]{4}$")
                if (!s.matches(phoneMatches)) {
                    PhoneNumberTextInputLayout.error = "올바른 휴대전화 번호가 아닙니다."
                } else {
                    PhoneNumberTextInputLayout.error = null // null은 에러 메시지를 지워주는 기능
                }
            }
        })


        // 회원가입 버튼 눌렀을때 클릭 이벤트
        button_Register_Apply.setOnClickListener {

            //이메일형식체크

            if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.text.toString()).matches()) {
                Toast.makeText(this, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show()
                editEmail.requestFocus()
                return@setOnClickListener
            }

            //비밀번호 유효성

            val passMatches = Regex("^(?=.*[a-zA-Z0-9])(?=.*[!@#\$%^*+=-]).{8,20}$")

            if (!editPassword.text.toString().matches(passMatches)) {
                Toast.makeText(this, "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show()
                editPassword.requestFocus()
                return@setOnClickListener
            }

            // 이름 적었는지 확인.

            if (editName.text.isEmpty()) {
                Toast.makeText(this, "이름을 작성하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
                editName.requestFocus()
                return@setOnClickListener
            }

            // 닉네임 적었는지 확인.

            if (editNickname.text.isEmpty()) {
                Toast.makeText(this, "닉네임을 작성하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
                editNickname.requestFocus()
                return@setOnClickListener
            }

            //핸드폰번호 유효성

            val phoneMatches = Regex("^01[016789][0-9]{3,4}[0-9]{4}$")
            if (!editPhone_number.text.toString().matches(phoneMatches)) {
                Toast.makeText(this, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
                editPhone_number.requestFocus()
                return@setOnClickListener
            }

            register(
                editEmail.text.toString(),
                editPassword.text.toString(),
                editName.text.toString(),
                editNickname.text.toString(),
                editPhone_number.text.toString()
            )

        }
    }

    // 툴바 사용 설정
    private fun setToolbar() {
        setSupportActionBar(RegisterToolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.title = "회원가입"
        supportActionBar!!.setDisplayShowTitleEnabled(true)    // 타이틀 안보이게 하기
    }

    // 회원가입.
    private fun register(
        email: String?,
        password: String?,
        name: String?,
        nickname: String?,
        phone_number: String?
    ) {
        compositeDisposable.add(myAPI.registerUser(email, password, name, nickname, phone_number)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.contains("encrypted_password")) {
                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
