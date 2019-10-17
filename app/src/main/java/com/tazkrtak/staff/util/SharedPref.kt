package com.tazkrtak.staff.util

import android.content.Context
import com.tazkrtak.staff.App

object SharedPref {

    private const val SHARED_PREF = "PREF"

    fun addString(key: String, value: String?) {
        val context = App.appContext!!
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String?): String {
        val context = App.appContext!!
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(key, "").toString()
    }

}