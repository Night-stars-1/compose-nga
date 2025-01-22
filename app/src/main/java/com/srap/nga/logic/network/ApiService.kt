package com.srap.nga.logic.network

import com.srap.nga.logic.model.CreateQRCodeResponse
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.QRCodeLoginResponse
import com.srap.nga.logic.model.RecTopicResponse
import com.srap.nga.logic.model.TopicCateGoryResponse
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.model.UserInfoResponse
import com.srap.nga.logic.model.UserSubjectResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    /**
     * 获取推荐话题
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=app_inter&__act=recmd_topic")
    fun getRecTopic(
        @Field("__output") output: Int = 14
    ): Call<RecTopicResponse>

    @FormUrlEncoded
    @POST("app_api.php?__lib=post&__act=list")
    fun getPost(
        @Field("tid") tid: Int,
        @Field("page") page: Int = 1,
    ): Call<PostResponse>

    @FormUrlEncoded
    @POST("app_api.php?__lib=home&__act=category")
    fun getTopicCateGory(
        @Field("__output") output: Int = 14
    ): Call<TopicCateGoryResponse>

    @FormUrlEncoded
    @POST("app_api.php?__lib=subject&__act=list")
    fun getTopicSubject(
        @Field("fid") fid: Int,
        @Field("page") page: Int = 1,
        @Field("__output") output: Int = 14
    ): Call<TopicSubjectResponse>

    @FormUrlEncoded
    @POST("app_api.php?__lib=user&__act=detail")
    fun getUserInfo(
        @Field("uid") uid: Int,
        @Field("__output") output: Int = 14
    ): Call<UserInfoResponse>

    @FormUrlEncoded
    @POST("app_api.php?__lib=user&__act=subjects")
    fun getUserSubject(
        @Field("uid") uid: Int,
        @Field("page") page: Int = 1,
        @Field("__output") output: Int = 14
    ): Call<UserSubjectResponse>

    @FormUrlEncoded
    @POST("nuke.php?__lib=login&__act=qrlogin_gen")
    fun createQRCode(
        @Field("__output") output: Int = 14
    ): Call<CreateQRCodeResponse>

    @FormUrlEncoded
    @POST("nuke.php?__lib=login&__act=login")
    fun qrCodeLogin(
        @Field("qrkey") qrKey: String,
        @Field("hiddenkey") hiddenKey: String,
        @Field("device") device: String = "",
        @Field("__output") output: Int = 14
    ): Call<QRCodeLoginResponse>
}