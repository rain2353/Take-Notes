package com.example.takenotes.Common

import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Model.Memo
import com.example.takenotes.Model.User

object Common {
        var UserInfomation : User? = null    // 로그인한 유저의 정보를 가지고 있는다.
        var SelectMemo : Memo? = null        // 사용자가 작성된 메모 리사이클러뷰 아이템을 선택하면 그 아이템에 대한 정보들을 가지고 있는다.



    val api: INodeJS
        get() {
            val retrofit = RetrofitClient.instance
            return retrofit.create(INodeJS::class.java)
        }
}