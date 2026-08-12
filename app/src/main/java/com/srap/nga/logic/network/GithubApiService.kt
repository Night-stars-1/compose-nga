package com.srap.nga.logic.network

import com.srap.nga.logic.model.GithubReleaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubApiService {

    @Headers("Accept: application/vnd.github+json")
    @GET("repos/${NetworkModule.GITHUB_REPO}/releases/latest")
    fun getLatestRelease(): Call<GithubReleaseResponse>
}
