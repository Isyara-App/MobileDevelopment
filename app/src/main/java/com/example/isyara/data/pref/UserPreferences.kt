package com.example.isyara.data.pref

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "token"
        private const val NAME = "name"
        private const val ID = "0"
        private const val IMAGE = "image"
    }

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun saveName(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString(NAME, name)
        editor.apply()
    }

    fun saveId(id: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ID, id)
        editor.apply()
    }

    fun saveImage(image: String) {
        val editor = sharedPreferences.edit()
        editor.putString(IMAGE, image)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getName(): String? {
        return sharedPreferences.getString(NAME, null)
    }

    fun getId(): String? {
        return sharedPreferences.getString(ID, null)
    }

    fun getImage(): String? {
        return sharedPreferences.getString(IMAGE, null)
    }


    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.remove(NAME)
        editor.remove(ID)
        editor.remove(IMAGE)
        editor.apply()
    }
}