package com.srap.nga.logic.network

import com.srap.nga.logic.model.CateGoryFavorResponse
import com.srap.nga.logic.model.CommonResponse
import com.srap.nga.logic.model.CreateQRCodeResponse
import com.srap.nga.logic.model.FavoriteContentResponse
import com.srap.nga.logic.model.FavoriteResponse
import com.srap.nga.logic.model.ForumBySearchResponse
import com.srap.nga.logic.model.SearchPromptResponse
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.QRCodeLoginResponse
import com.srap.nga.logic.model.RecTopicResponse
import com.srap.nga.logic.model.SubjectBySearchResponse
import com.srap.nga.logic.model.TopicCateGoryResponse
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.model.UserInfoResponse
import com.srap.nga.logic.model.UserSubjectResponse
import com.srap.nga.logic.model.base.BaseResponse
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
        @Field("page") page: Int = 1
    ): Call<RecTopicResponse>

    /**
     * 获取帖子内容
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=post&__act=list")
    fun getPost(
        @Field("tid") tid: Int,
        @Field("page") page: Int = 1,
    ): Call<PostResponse>

    /**
     * 获取社区列表
     */
    @POST("app_api.php?__lib=home&__act=category")
    fun getTopicCateGory(): Call<TopicCateGoryResponse>

    /**
     * 获取关注社区列表
     */
    @POST("nuke.php?__lib=forum_favor2&__act=get")
    fun getCateGoryFavor(): Call<CateGoryFavorResponse>

    /**
     * 关注社区
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=forum_favor2&__act=add")
    fun addCateGoryFavor(
        @Field("fid") fid: Int,
    ): Call<CommonResponse>
    /**
     * 取消关注社区
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=forum_favor2&__act=del")
    fun delCateGoryFavor(
        @Field("fid") fid: Int,
    ): Call<CommonResponse>

    /**
     * 获取社区内容
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=subject&__act=list")
    fun getTopicSubject(
        @Field("fid") fid: Int,
        @Field("page") page: Int = 1,
    ): Call<TopicSubjectResponse>

    /**
     * 获取用户信息
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=user&__act=detail")
    fun getUserInfo(
        @Field("uid") uid: Int,
    ): Call<UserInfoResponse>

    /**
     * 获取用户帖子
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=user&__act=subjects")
    fun getUserSubject(
        @Field("uid") uid: Int,
        @Field("page") page: Int = 1,
    ): Call<UserSubjectResponse>

    /**
     * 创建二维码
     */
    @POST("nuke.php?__lib=login&__act=qrlogin_gen")
    fun createQRCode(): Call<CreateQRCodeResponse>

    /**
     * 二维码登录
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=login&__act=login")
    fun qrCodeLogin(
        @Field("qrkey") qrKey: String,
        @Field("hiddenkey") hiddenKey: String,
        @Field("device") device: String = "",
    ): Call<QRCodeLoginResponse>


    /**
     * 搜索帖子
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=subject&__act=search")
    fun getSubjectBySearch(
        @Field("key") key: String,
        @Field("page") page: Int,
        /**
         * 数量
         */
        @Field("table") table: Int = 7,
    ): Call<SubjectBySearchResponse>


    /**
     * 搜索社区
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=forum&__act=search")
    fun getForumBySearch(
        @Field("key") key: String,
        @Field("page") page: Int,
    ): Call<ForumBySearchResponse>


    /**
     * 搜索提示词
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=search&__act=instant_search")
    fun getSearchPrompt(
        @Field("word") word: String,
    ): Call<SearchPromptResponse>

    /**
     * 获取收藏夹
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=topic_favor_v2&__act=list_folder")
    fun getFavorite(
        @Field("page") page: Int,
    ): Call<FavoriteResponse>

    /**
     * 获取收藏夹内容
     */
    @FormUrlEncoded
    @POST("app_api.php?__lib=favor&__act=all")
    fun getFavoriteContent(
        @Field("folder") folder: Int,
        @Field("page") page: Int,
    ): Call<FavoriteContentResponse>

    /**
     * 收藏
     */
    @FormUrlEncoded
    @POST("nuke.php?__lib=topic_favor_v2&__act=add")
    fun addFavorite(
        @Field("tid") tid: Int,
        @Field("folder") folder: Int,
    ): Call<CommonResponse>
}