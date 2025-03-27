package com.srap.nga.logic.repository

import android.util.Log
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.network.ApiService
import com.srap.nga.logic.network.NgaService
import com.srap.nga.logic.state.Code
import com.srap.nga.logic.state.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class NetworkRepo @Inject constructor(
    @NgaService
    private val apiService: ApiService,
) {
    val tag = "NetworkRepo"

    /**
     * 获取推荐话题
     */
    fun getRecTopic(page: Int = 1) = fire {
        apiService.getRecTopic(page).await()
    }

    /**
     * 获取帖子内容
     */
    fun getPost(id: Int, page: Int) = fire {
        apiService.getPost(id, page).await()
    }

    /**
     * 获取社区列表
     */
    fun getTopicCateGory() = fire {
        apiService.getTopicCateGory().await()
    }

    /**
     * 获取关注社区列表
     */
    fun getCateGoryFavor() = fire {
        apiService.getCateGoryFavor().await()
    }

    /**
     * 关注社区
     */
    fun addCateGoryFavor(fid: Int) = fire {
        apiService.addCateGoryFavor(fid).await()
    }

    /**
     * 取消关注社区
     */
    fun delCateGoryFavor(fid: Int) = fire {
        apiService.delCateGoryFavor(fid).await()
    }

    /**
     * 获取社区内容
     */
    fun getTopicSubject(id: Int, page: Int = 1) = fire {
        apiService.getTopicSubject(id, page).await()
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(id: Int) = fire {
        apiService.getUserInfo(id).await()
    }

    /**
     * 获取用户帖子
     */
    fun getUserSubject(id: Int, page: Int = 1) = fire {
        apiService.getUserSubject(id, page).await()
    }

    /**
     * 创建二维码
     */
    fun createQRCode() = fire {
        apiService.createQRCode().await()
    }

    /**
     * 二维码登录
     */
    fun qrCodeLogin(qrKey: String, hiddenKey: String) = fire {
        apiService.qrCodeLogin(qrKey, hiddenKey).await()
    }

    /**
     * 搜索社区
     */
    fun getForumBySearch(key: String, page: Int = 1) = fire {
        apiService.getForumBySearch(key, page).await()
    }

    /**
     * 搜索帖子
     */
    fun getSubjectBySearch(key: String, page: Int = 1) = fire {
        apiService.getSubjectBySearch(key, page).await()
    }

    /**
     * 搜索提示词
     */
    fun getSearchPrompt(word: String) = fire {
        apiService.getSearchPrompt(word).await()
    }

    /**
     * 获取收藏夹
     */
    fun getFavorite(page: Int = 0) = fire {
        apiService.getFavorite(page).await()
    }

    /**
     * 获取收藏夹内容
     */
    fun getFavoriteContent(folder: Int, page: Int) = fire {
        apiService.getFavoriteContent(folder, page).await()
    }

    /**
     * 收藏
     * @param tid 帖子ID
     * @param folder 收藏夹ID
     */
    fun addFavorite(tid: Int, folder: Int) = fire {
        apiService.addFavorite(tid, folder).await()
    }

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    private fun <T> fire(block: suspend () -> BaseResponse<T>): Flow<LoadingState<T>> = flow {
        val result = try {
            val response = block()
            if (response.code != Code.SUCCESS) {
                LoadingState.Error(response.msg)
            } else {
                LoadingState.Success(response as T)
            }
        } catch (e: Exception) {
            Log.e(tag, e.message, e)
            LoadingState.Error(e.message ?: "unknown error")
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}