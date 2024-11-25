package com.example.isyara.data.remote.retrofit

import com.example.isyara.data.remote.response.DictionarySentenceResponse
import com.example.isyara.data.remote.response.DictionaryWordResponse
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.remote.response.NewsResponse
import com.example.isyara.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("dictionary/letters")
    suspend fun searchLetters(
        @Header("Authorization") token: String,
        @Query("search") query: String
    ): DictionaryWordResponse

    @GET("dictionary/words")
    suspend fun searchSentence(
        @Header("Authorization") token: String,
        @Query("search") query: String
    ): DictionarySentenceResponse

    @GET("news")
    suspend fun news(@Header("Authorization") token: String): NewsResponse
}
