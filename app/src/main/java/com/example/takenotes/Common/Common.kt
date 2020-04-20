package com.example.takenotes.Common

import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Model.*

object Common {
    var UserInfomation : User? = null    // 로그인한 유저의 정보를 가지고 있는다.
    var SelectMemo : Memo? = null        // 사용자가 작성된 메모 리사이클러뷰 아이템을 선택하면 그 아이템에 대한 정보들을 가지고 있는다.
    var SelectPicture : Image? = null   // 사용자가 업로드한 사진 리스트중 선택한 아이템의 정보를 가지고 있다.
    var SelectVideo: Video? = null      // 사용자가 업로드한 동영상 리스트중 리사이클러뷰에서 선택한 아이템의 정보를 가지고 있다.
    var SelectAudio: Audio? = null      // 사용자가 업로드한 음성녹음 리스트에서 선택한 아이템의 정보를 가지고 있다.

    val api: INodeJS
        get() {
            val retrofit = RetrofitClient.instance 
            return retrofit.create(INodeJS::class.java)
        }
}
