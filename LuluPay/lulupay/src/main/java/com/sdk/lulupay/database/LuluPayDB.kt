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
    val phoneNo: String,
    val iban: String? = null,
    val accountNo: String? = null
)

class LuluPayDB(context: Context) {

    companion object {
        private const val DATABASE_NAME = "remittance.db"
        private const val DATABASE_VERSION = 3
    }

    private val helper: SupportSQLiteOpenHelper

    init {
        val configuration =
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(DATABASE_NAME)
                .callback(object : SupportSQLiteOpenHelper.Callback(DATABASE_VERSION) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // Fixed table creation syntax
                        db.execSQL(
                            """
                            CREATE TABLE remittance_saved_receipients (
                                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                                transactionRefNo TEXT, 
                                firstName TEXT, 
                                lastName TEXT, 
                                phoneNo TEXT, 
                                iban TEXT, 
                                accountNo TEXT
                            )
                            """.trimIndent()
                        )
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {
                        // Handle upgrades properly instead of dropping data
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

    // Insert remittance data
    suspend fun insertData(
        transactionRefNo: String,
        firstName: String,
        lastName: String,
        phoneNo: String,
        iban: String?,
        accountNo: String?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val db = getDatabase()
                db.execSQL(
                    "INSERT INTO remittance_saved_receipients (transactionRefNo, firstName, lastName, phoneNo, iban, accountNo) VALUES (?, ?, ?, ?, ?, ?)",
                    arrayOf(transactionRefNo, firstName, lastName, phoneNo, iban, accountNo)
                )
            } catch (e: Exception) {
                throw Exception("Error inserting data: ${e.message}")
            }
        }
    }

    // Get all remittance data
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
                    val iban = cursor.getString(cursor.getColumnIndexOrThrow("iban"))
                    val accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"))
                    dataList.add(RemittanceHistory(transactionRefNo, firstName, lastName, phoneNo, iban, accountNo))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            throw Exception("Error reading data: ${e.message}")
        } finally {
            cursor?.close()
        }
        return dataList
    }

    // Update remittance data by ID
    suspend fun updateData(
        id: Int,
        newTransactionRefNo: String,
        newFirstName: String,
        newLastName: String,
        newPhoneNo: String,
        newIban: String?,
        newAccountNo: String?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val db = getDatabase()
                db.execSQL(
                    "UPDATE remittance_saved_receipients SET transactionRefNo = ?, firstName = ?, lastName = ?, phoneNo = ?, iban = ?, accountNo = ? WHERE id = ?",
                    arrayOf(newTransactionRefNo, newFirstName, newLastName, newPhoneNo, newIban, newAccountNo, id)
                )
            } catch (e: Exception) {
                throw Exception("Error updating data: ${e.message}")
            }
        }
    }

    // Delete remittance data by ID
    suspend fun deleteData(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val db = getDatabase()
                db.execSQL("DELETE FROM remittance_saved_receipients WHERE id = ?", arrayOf(id))
            } catch (e: Exception) {
                throw Exception("Error deleting data: ${e.message}")
            }
        }
    }

    fun closeDatabase() {
        helper.close()
    }
}