package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DeleteImageProfileResponse
import com.example.isyara.data.remote.response.ProfileResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository private constructor(private val apiService: ApiService) {

    suspend fun updateProfile(
        token: String,
        id: Int,
        imageFile: File? = null,
        name: String,

        ): Result<ProfileResponse> {
        val nameRequestBody = name
            .toRequestBody("text/plain".toMediaTypeOrNull())


        val imagePart = imageFile?.let {
            val imageRequestBody = it
                .asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, imageRequestBody)
        }
        return safeApiCall {
            apiService.updateProfile("Bearer $token", id, imagePart, nameRequestBody)
        }
    }

    suspend fun deleteImageProfile(
        token: String,
        id: Int
    ): Result<DeleteImageProfileResponse> {
        return safeApiCall {
            apiService.deleteImageProfile("Bearer $token", id)
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}