package com.example.takenotes.Common

import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.RetrofitClient

object Common {





    val api: INodeJS
        get() {
            val retrofit = RetrofitClient.instance
            return retrofit.create(INodeJS::class.java)
        }
}