package com.example.isyara.data.remote.retrofit

import com.example.isyara.data.remote.response.CheckAnswerResponse
import com.example.isyara.data.remote.response.CheckCompletionResponse
import com.example.isyara.data.remote.response.DeleteAccountResponse
import com.example.isyara.data.remote.response.DictionarySentenceResponse
import com.example.isyara.data.remote.response.DictionaryWordResponse
import com.example.isyara.data.remote.response.LearningStatusResponse
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.remote.response.LogoutResponse
import com.example.isyara.data.remote.response.MessageListResponse
import com.example.isyara.data.remote.response.ProfileResponse
import com.example.isyara.data.remote.response.QuestionResponse
import com.example.isyara.data.remote.response.QuizByIdResponse
import com.example.isyara.data.remote.response.QuizResponse
import com.example.isyara.data.remote.response.RegisterResponse
import com.example.isyara.data.remote.response.SendMessageResponse
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

    // === Auth ===

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String = "user"
    ): LoginResponse

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse

    // === Profile ===

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @Multipart
    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part? = null,
        @Part("name") name: RequestBody
    ): ProfileResponse

    @DELETE("profile")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): DeleteAccountResponse

    // === Dictionary ===

    @GET("dictionary/letters")
    suspend fun searchLetters(
        @Header("Authorization") token: String,
        @Query("search") query: String,
        @Query("is_bisindo") isBisindo: String? = null
    ): DictionaryWordResponse

    @GET("dictionary/words")
    suspend fun searchSentence(
        @Header("Authorization") token: String,
        @Query("search") query: String,
        @Query("is_bisindo") isBisindo: String? = null
    ): DictionarySentenceResponse

    @POST("dictionary/letters/{id}/learning-status")
    suspend fun toggleLetterLearningStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body requestBody: LearningStatusRequest
    ): LearningStatusResponse

    @POST("dictionary/words/{id}/learning-status")
    suspend fun toggleWordLearningStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body requestBody: LearningStatusRequest
    ): LearningStatusResponse

    data class LearningStatusRequest(
        @SerializedName("is_knowing") val isKnowing: Boolean
    )

    // === Quiz ===

    @GET("quiz/levels")
    suspend fun allLevels(@Header("Authorization") token: String): QuizResponse

    @GET("quiz/levels/{levelId}")
    suspend fun levelById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int
    ): QuizByIdResponse

    @GET("quiz/levels/{levelId}/questions/{questionId}")
    suspend fun questionById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int,
        @Path("questionId") questionId: Int
    ): QuestionResponse

    @POST("quiz/levels/{levelId}/questions/{questionId}/answer")
    suspend fun checkAnswerById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int,
        @Path("questionId") questionId: Int,
        @Body requestBody: SelectedOptionRequest
    ): CheckAnswerResponse

    data class SelectedOptionRequest(
        @SerializedName("selected_option") val selectedOption: String
    )

    @GET("quiz/levels/{levelId}/completion")
    suspend fun checkCompletionById(
        @Header("Authorization") token: String,
        @Path("levelId") levelId: Int
    ): CheckCompletionResponse

    // === Messages ===

    @GET("messages")
    suspend fun getMessages(
        @Header("Authorization") token: String
    ): MessageListResponse

    @POST("messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body requestBody: SendMessageRequest
    ): SendMessageResponse

    data class SendMessageRequest(
        @SerializedName("message") val message: String
    )
}
