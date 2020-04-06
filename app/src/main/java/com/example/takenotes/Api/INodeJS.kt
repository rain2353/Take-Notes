package com.example.takenotes.Api


import androidx.test.runner.permission.RequestPermissionCallable
import com.example.takenotes.Model.Image
import com.example.takenotes.Model.Memo
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.adapter.rxjava2.Result
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

    // 사용자가 선택한 이미지 업로드

    @POST("ImageUpload")
    @Multipart
    fun ImageUpload(
        @Part files : ArrayList<MultipartBody.Part>,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?
    ) : Call<String>

    // 업로드한 사진 리스트 불러오기
    @GET("ImageList/{email}")
    fun ImageList(
        @Path("email") email: String?
    ): Observable<List<Image>>

    // 사진 삭제
    @POST("DeletePicture")
    @FormUrlEncoded
    fun DeletePicture(@Field("num") num: Int?): Observable<String>
}