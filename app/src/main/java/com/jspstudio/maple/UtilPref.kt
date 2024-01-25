package com.jspstudio.maple

import android.content.Context
import android.content.SharedPreferences

object UtilPref {
    private const val PREFERENCE_NAME = "com.jspstudio.maple.util.preferences"
    const val SET_MUSIC = "setMusic"
    const val SET_THEME = "setTheme"

    private fun pref(context: Context) : SharedPreferences { return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) }

    fun getDataInt(context: Context, key: String): Int { return pref(context).getInt(key, 0) }
    fun setDataInt(context: Context, key: String, value: Int) { pref(context).edit().putInt(key, value).apply() }

    fun getDataLong(context: Context, key: String): Long { return pref(context).getLong(key, 0L) }
    fun setDataLong(context: Context, key: String, value: Long) { pref(context).edit().putLong(key, value).apply() }

    fun getDataString(context: Context, key: String): String? { return pref(context).getString(key, "") }
    fun setDataString(context: Context, key: String, value: String) { pref(context).edit().putString(key, value).apply() }

    fun getDataBoolean(context: Context, key: String): Boolean { return pref(context).getBoolean(key, true) }
    fun setDataBoolean(context: Context, key: String, value: Boolean) { pref(context).edit().putBoolean(key, value).apply() }

    fun getNewMember(context: Context, key: String): Boolean { return pref(context).getBoolean(key, true) }
    fun setNewMember(context: Context, key: String, value: Boolean) { pref(context).edit().putBoolean(key, value).apply() }
}