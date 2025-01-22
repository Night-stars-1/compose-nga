package com.srap.nga.logic.model
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class UserInfoResponse(
    @SerializedName("code")
    override val code: Code,
    @SerializedName("result")
    val result: Result,
    @SerializedName("msg")
    override val msg: String,
) : BaseResponse<UserInfoResponse>(code, msg){
    data class Result(
        @SerializedName("adfree")
        val adfree: Int,
        @SerializedName("_admin")
        val admin: Int,
        /**
         * 头像链接
         */
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("bit")
        val bit: Int,
        @SerializedName("clientChat")
        val clientChat: Int,
        @SerializedName("conferred_title")
        val conferredTitle: String,
        /**
         * 礼物
         */
        @SerializedName("count_gift")
        val countGift: Int,
        /**
         * 获赞
         */
        @SerializedName("count_like")
        val countLike: Int,
        @SerializedName("faction")
        val faction: Int,
        /**
         * 声望 * 10
         */
        @SerializedName("fame")
        val fame: Int,
        /**
         * 被关注
         */
        @SerializedName("follow_by_num")
        val followByNum: Int,
        @SerializedName("fuck_money")
        val fuckMoney: Int,
        @SerializedName("gender")
        val gender: Int,
        @SerializedName("gid")
        val gid: Int,
        @SerializedName("_greater")
        val greater: Int,
        /**
         * 身份组
         */
        @SerializedName("group")
        val group: String,
        @SerializedName("groupid")
        val groupid: Int,
        @SerializedName("honor")
        val honor: String,
        /**
         * IP地址
         */
        @SerializedName("ipLoc")
        val ipLoc: String,
        @SerializedName("lastpost")
        val lastpost: Int,
        @SerializedName("lastvisit")
        val lastvisit: Int,
        @SerializedName("_lesser")
        val lesser: Int,
        @SerializedName("live")
        val live: String,
        /**
         * 勋章
         */
        @SerializedName("medal")
        val medal: List<Medal>,
        @SerializedName("memberid")
        val memberid: Int,
        @SerializedName("money")
        val money: Int,
        @SerializedName("muteTime")
        val muteTime: Int,
        @SerializedName("posts")
        val posts: Int,
        @SerializedName("regdate")
        val regdate: Int,
        /**
         * 声望
         */
        @SerializedName("rvrc")
        val rvrc: String,
        /**
         * 签名
         */
        @SerializedName("signature")
        val signature: String,
        @SerializedName("_super")
        val superX: Int,
        @SerializedName("thisvisit")
        val thisvisit: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("uid")
        val uid: Int,
        @SerializedName("umengChat")
        val umengChat: Int,
        @SerializedName("username")
        val username: String,
        @SerializedName("weibo")
        val weibo: String
    ) {
        data class Medal(
            /**
             * 描述
             */
            @SerializedName("dscp")
            val description: String,
            /**
             * 图标链接
             */
            @SerializedName("icon")
            val icon: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String
        )
    }
}

