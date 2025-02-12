package com.sdk.lulupay.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferencesHelper(context: Context) {

    var sharedPreferences: SharedPreferences

    init {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun save(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    
    fun saveStringSet(key: String, set: Set<String>) {
    sharedPreferences.edit().putStringSet(key, set).apply()
}
   
   fun retrieveStringSet(key: String): Set<String> {
        return sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
    }

   fun getStringByIndex(key: String, index: Int): String? {
        val stringSet = retrieveStringSet(key)
        val list = stringSet.toList() // Convert the Set to a List
        return if (index >= 0 && index < list.size) {
            list[index] // Return the element at the specified index
        } else {
            null // Return null if the index is out of bounds
        }
    }
    
    fun getStringByIndexSize(key: String): Int{
        val stringSet = retrieveStringSet(key)
        val list = stringSet.toList() // Convert the Set to a List
        return list.size
    }

    fun retrieve(key: String): Int {
        return sharedPreferences.getInt(key, 0) // Return 0 as default if the key does not exist
    }
}