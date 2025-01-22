package com.srap.nga.logic.network

import com.google.gson.GsonBuilder
import com.srap.nga.logic.state.Code
import com.srap.nga.logic.state.CodeAdapter
import com.srap.nga.utils.interceptor.AuthInterceptor
import com.srap.nga.utils.interceptor.SignInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NgaService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val NGA_BASE_URL = "https://ngabbs.com"
    const val NGA_APP_ICON_URL = "https://img4.nga.178.com/ngabbs/nga_classic/f/app/%s.png"
    const val NGA_ATTACHMENTS_URL = "https://img.nga.178.com/attachments/%s"
    const val NGA_SMILE_URL = "https://img4.nga.178.com/ngabbs/post/smile/%s"
    const val NGA_QR_LOGIN_URL = "https://ngabbs.com/nuke.php?__lib=login&__act=qrlogin_ui&qrkey=%s"

    @NgaService
    @Singleton
    @Provides
    fun provideNgaService(@NgaService retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @NgaService
    @Singleton
    @Provides
    fun provideNgaServiceRetrofit(@NgaService okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(Code::class.java, CodeAdapter()) // 注册自定义 TypeAdapter
            .create()

        return Retrofit.Builder()
            .baseUrl(NGA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // json处理器
            .client(okHttpClient)
            .build()
    }

    @NgaService
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(SignInterceptor()) // 添加请求签名拦截器
            .addInterceptor(AuthInterceptor())
            .build()
    }

}