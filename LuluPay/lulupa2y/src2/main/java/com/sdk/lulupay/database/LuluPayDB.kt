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
    val transactionRefNo: String,
    val firstName: String,
    val lastName: String,
    val phoneNo: String
)

class LuluPayDB(context: Context) {

  companion object {
    private const val DATABASE_NAME = "remittance.db"
    private const val DATABASE_VERSION = 2
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
                        "CREATE TABLE remittance_saved_receipients (" +
                            "id INTEGER PRIMARY KEY, " +
                            "transactionRefNo TEXT, " +
                            "firstName TEXT, " +
                            "lastName TEXT, " +
                            "phoneNo TEXT)")
                  }

                  override fun onUpgrade(
                      db: SupportSQLiteDatabase,
                      oldVersion: Int,
                      newVersion: Int
                  ) {
                    // Upgrade database (here we drop and recreate the table for simplicity)
                    db.execSQL("DROP TABLE IF EXISTS remittance_saved_receipients")
                    onCreate(db)
                  }
                })
            .build()

    helper = FrameworkSQLiteOpenHelperFactory().create(configuration)
  }

  private fun getDatabase(): SupportSQLiteDatabase {
    return helper.writableDatabase
  }

  // Insert remittance data with 4 fields (transactionRefNo, firstName, lastName, phoneNo)
  suspend fun insertData(
      transactionRefNo: String,
      firstName: String,
      lastName: String,
      phoneNo: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "INSERT INTO remittance_saved_receipients (transactionRefNo, firstName, lastName, phoneNo) VALUES (?, ?, ?, ?)",
          arrayOf(transactionRefNo, firstName, lastName, phoneNo))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Get all remittance data (transactionRefNo, firstName, lastName, phoneNo)
  suspend fun getAllData(): List<RemittanceHistory> {
    val dataList = mutableListOf<RemittanceHistory>()
    val db = getDatabase()
    var cursor: Cursor? = null
    try {
      cursor = db.query("SELECT * FROM remittance_saved_receipients")
      if (cursor.moveToFirst()) {
        do {
          val transactionRefNo = cursor.getString(cursor.getColumnIndexOrThrow("transactionRefNo"))
          val firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"))
          val lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"))
          val phoneNo = cursor.getString(cursor.getColumnIndexOrThrow("phoneNo"))
          dataList.add(RemittanceHistory(transactionRefNo, firstName, lastName, phoneNo))
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
      newTransactionRefNo: String,
      newFirstName: String,
      newLastName: String,
      newPhoneNo: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "UPDATE remittance_saved_receipients SET transactionRefNo = ?, firstName = ?, lastName = ?, phoneNo = ? WHERE id = ?",
          arrayOf(newTransactionRefNo, newFirstName, newLastName, newPhoneNo, id))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Delete remittance data by id
  suspend fun deleteData(id: Int) {
    withContext(Dispatchers.IO) {
      try {
        val db = getDatabase()
        db.execSQL("DELETE FROM remittance_saved_receipients WHERE id = ?", arrayOf(id))
      } catch (e: Exception) {
        throw Exception(e.message)
      }
    }
  }

  fun closeDatabase() {
    helper.close()
  }
}
