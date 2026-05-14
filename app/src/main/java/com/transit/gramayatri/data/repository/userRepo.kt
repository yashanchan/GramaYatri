package com.transit.gramayatri.data.repository

import android.content.Context
import android.content.SharedPreferences


class userRepo(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gramayatri_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NAME  = "user_name"
        private const val KEY_PHONE = "user_phone"
    }
    fun isProfileSaved(): Boolean {
        return prefs.getString(KEY_NAME, null)?.isNotBlank() == true
    }

    fun getUserName(): String  = prefs.getString(KEY_NAME, "")  ?: ""
    fun getUserPhone(): String = prefs.getString(KEY_PHONE, "") ?: ""

    fun saveProfile(name: String, phone: String) {
        prefs.edit()
            .putString(KEY_NAME, name.trim())
            .putString(KEY_PHONE, phone.trim())
            .apply()
    }

    fun clearProfile() {
        prefs.edit().clear().apply()
    }
}