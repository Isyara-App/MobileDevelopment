package com.example.isyara.data.remote.retrofit

import com.example.isyara.data.remote.response.CheckAnswerResponse
import com.example.isyara.data.remote.response.CheckCompletionResponse
import com.example.isyara.data.remote.response.CommunityResponse
import com.example.isyara.data.remote.response.ContactResponse
import com.example.isyara.data.remote.response.DeleteImageProfileResponse
import com.example.isyara.data.remote.response.DictionarySentenceResponse
import com.example.isyara.data.remote.response.DictionaryWordResponse
import com.example.isyara.data.remote.response.EventResponse
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.remote.response.LogoutResponse
import com.example.isyara.data.remote.response.NewsResponse
import com.example.isyara.data.remote.response.ProfileResponse
import com.example.isyara.data.remote.response.QuestionResponse
import com.example.isyara.data.remote.response.QuizByIdResponse
import com.example.isyara.data.remote.response.QuizResponse
import com.example.isyara.data.remote.response.RegisterResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

    @POST("api/logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse

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

    @GET("events")
    suspend fun events(@Header("Authorization") token: String): EventResponse

    @GET("community")
    suspend fun community(@Header("Authorization") token: String): CommunityResponse

    @GET("/quiz/levels")
    suspend fun allLevels(@Header("Authorization") token: String): QuizResponse

    @GET("/quiz/levels/{levelId}")
    suspend fun levelById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int
    ): QuizByIdResponse

    @GET("/quiz/levels/{levelId}/questions/{questionId}")
    suspend fun questionById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int,
        @Path("questionId") questionId: Int
    ): QuestionResponse

    @POST("/quiz/levels/{levelId}/questions/{questionId}/answer")
    suspend fun checkAnswerById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int,
        @Path("questionId") questionId: Int,
        @Body requestBody: SelectedOptionRequest
    ): CheckAnswerResponse

    // Tambahkan data class untuk request body
    data class SelectedOptionRequest(
        @SerializedName("selected_option") val selectedOption: String
    )

    @GET("/quiz/levels/{levelId}/completion")
    suspend fun checkCompletionById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int
    ): CheckCompletionResponse

    @FormUrlEncoded
    @POST("/contact")
    suspend fun contact(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("message") message: String
    ): ContactResponse

    @Multipart
    @PUT("/profile/{id}")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part image: MultipartBody.Part? = null,
        @Part("name") name: RequestBody
    ): ProfileResponse

    @DELETE("/profile/{id}/picture")
    suspend fun deleteImageProfile(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DeleteImageProfileResponse
}
