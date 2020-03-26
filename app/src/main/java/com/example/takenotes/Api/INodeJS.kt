package com.example.takenotes.Api


import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface INodeJS {


    // 회원가입.
    @POST("register")
    @FormUrlEncoded
    fun registerUser(
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("name") name: String?,
        @Field("nickname") nickname: String?,
        @Field("phone_number") phone_number: String?
    ): Observable<String>

    // 로그인
    @POST("login")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Observable<String>
}