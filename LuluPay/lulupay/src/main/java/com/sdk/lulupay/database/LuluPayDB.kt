package com.sdk.lulupay.database

import android.content.Context
import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data class for representing the remittance history
data class RemittanceHistory(
    val senderName: String,
    val channelName: String,
    val branchCode: String,
    val companyCode: String
)

class LuluPayDB(context: Context) {

  companion object {
    private const val DATABASE_NAME = "remittance.db"
    private const val DATABASE_VERSION = 1
  }

  private val helper: SupportSQLiteOpenHelper

  init {
    val configuration =
        SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(DATABASE_NAME)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(DATABASE_VERSION) {
                  override fun onCreate(db: SupportSQLiteDatabase) {
                    // Create the table with the required columns
                    db.execSQL(
                        "CREATE TABLE remittance_history (" +
                            "id INTEGER PRIMARY KEY, " +
                            "senderName TEXT, " +
                            "channelName TEXT, " +
                            "branchCode TEXT, " +
                            "companyCode TEXT)")
                  }

                  override fun onUpgrade(
                      db: SupportSQLiteDatabase,
                      oldVersion: Int,
                      newVersion: Int
                  ) {
                    // Upgrade database (here we drop and recreate the table for simplicity)
                    db.execSQL("DROP TABLE IF EXISTS remittance_history")
                    onCreate(db)
                  }
                })
            .build()

    helper = FrameworkSQLiteOpenHelperFactory().create(configuration)
  }

  private fun getDatabase(): SupportSQLiteDatabase {
    return helper.writableDatabase
  }

  // Insert remittance data with 4 fields (senderName, channelName, branchCode, companyCode)
  suspend fun insertData(
      senderName: String,
      channelName: String,
      branchCode: String,
      companyCode: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "INSERT INTO remittance_history (senderName, channelName, branchCode, companyCode) VALUES (?, ?, ?, ?)",
          arrayOf(senderName, channelName, branchCode, companyCode))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Get all remittance data (senderName, channelName, branchCode, companyCode)
  suspend fun getAllData(): List<RemittanceHistory> {
    val dataList = mutableListOf<RemittanceHistory>()
    val db = getDatabase()
    var cursor: Cursor? = null
    try {
      cursor = db.query("SELECT * FROM remittance_history")
      if (cursor.moveToFirst()) {
        do {
          val senderName = cursor.getString(cursor.getColumnIndexOrThrow("senderName"))
          val channelName = cursor.getString(cursor.getColumnIndexOrThrow("channelName"))
          val branchCode = cursor.getString(cursor.getColumnIndexOrThrow("branchCode"))
          val companyCode = cursor.getString(cursor.getColumnIndexOrThrow("companyCode"))
          dataList.add(RemittanceHistory(senderName, channelName, branchCode, companyCode))
        } while (cursor.moveToNext())
      }
    } catch (e: Exception) {
      throw Exception("Error reading data: ${e.message}")
    } finally {
      cursor?.close()
    }
    return dataList
  }

  // Update remittance data by id
  suspend fun updateData(
      id: Int,
      newSenderName: String,
      newChannelName: String,
      newBranchCode: String,
      newCompanyCode: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "UPDATE remittance_history SET senderName = ?, channelName = ?, branchCode = ?, companyCode = ? WHERE id = ?",
          arrayOf(newSenderName, newChannelName, newBranchCode, newCompanyCode, id))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Delete remittance data by id
  suspend fun deleteData(id: Int) {
    withContext(Dispatchers.IO) {
      try {
        val db = getDatabase()
        db.execSQL("DELETE FROM remittance_history WHERE id = ?", arrayOf(id))
      } catch (e: Exception) {
        throw Exception(e.message)
      }
    }
  }

  fun closeDatabase() {
    helper.close()
  }
}
