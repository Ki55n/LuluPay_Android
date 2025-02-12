package com.sdk.lulupay.database

import android.content.Context
import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Data class for representing the remittance history
data class RemittanceTransactionHistory(
    val transactionRefNo: String,
    val firstName: String,
    val lastName: String,
    val transactionState: String
)

class LuluPayDB2(context: Context) {

  companion object {
    private const val DATABASE_NAME = "remittance_transaction_state.db"
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
                        "CREATE TABLE transaction_state (" +
                            "id INTEGER PRIMARY KEY, " +
                            "transactionRefNo TEXT, " +
                            "firstName TEXT, " +
                            "lastName TEXT, " +
                            "transactionState TEXT)")
                  }

                  override fun onUpgrade(
                      db: SupportSQLiteDatabase,
                      oldVersion: Int,
                      newVersion: Int
                  ) {
                    // Upgrade database (here we drop and recreate the table for simplicity)
                    db.execSQL("DROP TABLE IF EXISTS transaction_state")
                    onCreate(db)
                  }
                })
            .build()

    helper = FrameworkSQLiteOpenHelperFactory().create(configuration)
  }

  private fun getDatabase(): SupportSQLiteDatabase {
    return helper.writableDatabase
  }

  // Insert remittance data with 4 fields (transactionRefNo, firstName, lastName, transactionState)
  suspend fun insertData(
      transactionRefNo: String,
      firstName: String,
      lastName: String,
      transactionState: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "INSERT INTO transaction_state (transactionRefNo, firstName, lastName, transactionState) VALUES (?, ?, ?, ?)",
          arrayOf(transactionRefNo, firstName, lastName, transactionState))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Get all remittance data (transactionRefNo, firstName, lastName, transactionState)
  suspend fun getAllData(): List<RemittanceTransactionHistory> {
    val dataList = mutableListOf<RemittanceTransactionHistory>()
    val db = getDatabase()
    var cursor: Cursor? = null
    try {
      cursor = db.query("SELECT * FROM transaction_state")
      if (cursor.moveToFirst()) {
        do {
          val transactionRefNo = cursor.getString(cursor.getColumnIndexOrThrow("transactionRefNo"))
          val firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"))
          val lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"))
          val transactionState = cursor.getString(cursor.getColumnIndexOrThrow("transactionState"))
          dataList.add(RemittanceTransactionHistory(transactionRefNo, firstName, lastName, transactionState))
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
      newTransactionState: String
  ) {
    try {
      val db = getDatabase()
      db.execSQL(
          "UPDATE transaction_state SET transactionRefNo = ?, firstName = ?, lastName = ?, transactionState = ? WHERE id = ?",
          arrayOf(newTransactionRefNo, newFirstName, newLastName, newTransactionState, id))
    } catch (e: Exception) {
      throw Exception(e.message)
    }
  }

  // Delete remittance data by id
  suspend fun deleteData(id: Int) {
    withContext(Dispatchers.IO) {
      try {
        val db = getDatabase()
        db.execSQL("DELETE FROM transaction_state WHERE id = ?", arrayOf(id))
      } catch (e: Exception) {
        throw Exception(e.message)
      }
    }
  }

  fun closeDatabase() {
    helper.close()
  }
}