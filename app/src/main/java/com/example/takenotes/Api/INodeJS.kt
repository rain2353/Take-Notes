package com.example.takenotes.Api


import androidx.test.runner.permission.RequestPermissionCallable
import com.example.takenotes.Model.Audio
import com.example.takenotes.Model.Image
import com.example.takenotes.Model.Memo
import com.example.takenotes.Model.Video
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

    // 사진 수정
    @POST("ImageModify")
    @Multipart
    fun ImageModify(
        @Part ("num") num: Int?,
        @Part files : ArrayList<MultipartBody.Part>,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?,
        @Part ("created_at") created_at: String?
    ) : Call<String>

    // 사진 제외하고 텍스트만 수정
    @POST("ImageTextModify")
    @FormUrlEncoded
    fun ImageTextModify(
        @Field("num") num: Int?,
        @Field("email") email:String?,
        @Field("title") title:String?,
        @Field("content") content:String?,
        @Field("file") file:String?,
        @Field("file1") file1:String?,
        @Field("file2") file2:String?,
        @Field("file3") file3:String?,
        @Field("file4") file4:String?,
        @Field("file5") file5:String?,
        @Field("file6") file6:String?,
        @Field("file7") file7:String?,
        @Field("file8") file8:String?,
        @Field("file9") file9:String?,
        @Field("created_at") created_at:String?
    ): Observable<String>

    // 사진 삭제
    @POST("DeletePicture")
    @FormUrlEncoded
    fun DeletePicture(@Field("num") num: Int?): Observable<String>

    // 사용자가 선택한 동영상 업로드
    @POST("VideoUpload")
    @Multipart
    fun VideoUpload(
        @Part video : MultipartBody.Part,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?
    ) : Call<String>

    // 업로드한 동영상 리스트 불러오기
    @GET("VideoList/{email}")
    fun VideoList(
        @Path("email") email: String?
    ): Observable<List<Video>>

    // 사용자가 수정한 동영상 업로드
    @POST("VideoModify")
    @Multipart
    fun VideoModify(
        @Part ("num") num: Int?,
        @Part video : MultipartBody.Part,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?,
        @Part ("created_at") created_at: String?
    ) : Call<String>

    // 동영상 제외하고 텍스트만 수정
    @POST("VideoTextModify")
    @FormUrlEncoded
    fun VideoTextModify(
        @Field("num") num: Int?,
        @Field("email") email:String?,
        @Field("title") title:String?,
        @Field("content") content:String?,
        @Field("video") video:String?,
        @Field("created_at") created_at:String?
    ): Observable<String>

    // 동영상 삭제
    @POST("DeleteVideo")
    @FormUrlEncoded
    fun DeleteVideo(@Field("num") num: Int?): Observable<String>


    // 사용자가 녹음한 음성파일을 업로드한다.
    @POST("VoiceUpload")
    @Multipart
    fun VoiceUpload(
        @Part audio : MultipartBody.Part,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?
    ) : Call<String>

    // 업로드한 음성녹음 리스트 불러오기
    @GET("AudioList/{email}")
    fun AudioList(
        @Path("email") email: String?
    ): Observable<List<Audio>>


    // 사용자가 수정한 음성녹음 파일 업로드
    @POST("AudioModify")
    @Multipart
    fun AudioModify(
        @Part ("num") num: Int?,
        @Part video : MultipartBody.Part,
        @Part ("email") email: String?,
        @Part ("title") title: String?,
        @Part ("content") content: String?,
        @Part ("created_at") created_at: String?
    ) : Call<String>

    // 음성녹음 파일 제외하고 수정
    @POST("AudioTextModify")
    @FormUrlEncoded
    fun AudioTextModify(
        @Field("num") num: Int?,
        @Field("email") email:String?,
        @Field("title") title:String?,
        @Field("content") content:String?,
        @Field("audio") audio:String?,
        @Field("created_at") created_at:String?
    ): Observable<String>

    // 음성녹음 삭제
    @POST("DeleteAudio")
    @FormUrlEncoded
    fun DeleteAudio(@Field("num") num: Int?): Observable<String>
}