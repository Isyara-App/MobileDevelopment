package com.example.isyara.data.pref

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "token"
        private const val NAME = "name"
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


    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getName(): String? {
        return sharedPreferences.getString(NAME, null)
    }


    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.remove(NAME)
        editor.apply()
    }
}