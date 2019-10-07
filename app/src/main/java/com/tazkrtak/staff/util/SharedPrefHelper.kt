package com.tazkrtak.staff.util

import android.content.Context

object SharedPrefHelper {

    private const val SHARED_PREF = "SECRETS"

    fun addString(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(context: Context, key: String): String {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(key, "").toString()
    }

    fun removeString(context: Context, key: String) {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }

    fun exists(context: Context, key: String): Boolean {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPref.contains(key)
    }

}