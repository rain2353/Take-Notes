package com.example.takenotes.Api


import com.example.takenotes.Model.Memo
import io.reactivex.Observable
import retrofit2.http.*

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

    // 메모 작성
    @POST("writeMemo")
    @FormUrlEncoded
    fun WriteMemoUpload(
        @Field("email") email:String?,
        @Field("title") title:String?,
        @Field("content") content:String?
    ): Observable<String>

    // 작성한 메모 리스트 불러오기
    @GET("MemoList/{email}")
    fun MemoList(
        @Path("email") email: String?
    ): Observable<List<Memo>>

    // 메모 수정
    @POST("ChangeMemo")
    @FormUrlEncoded
    fun ChangeMemoUpload(
        @Field("num") num: Int?,
        @Field("email") email:String?,
        @Field("title") title:String?,
        @Field("content") content:String?,
        @Field("created_at") created_at:String?
    ): Observable<String>

    // 메모 삭제
    @POST("DeleteMemo")
    @FormUrlEncoded
    fun DeleteMemo(@Field("num") num: Int?): Observable<String>
}