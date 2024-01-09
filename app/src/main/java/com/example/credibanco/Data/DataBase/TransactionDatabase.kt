package com.example.credibanco.Data.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.credibanco.Data.Model.LocalDatabaseAuthorization

class TransactionDatabase(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "transaction_database"
            private const val DATABASE_VERSION = 1

            private const val TABLE_NAME = "transactions"
            private const val COLUMN_RECEIPT_ID = "receipt_id"
            private const val COLUMN_RRN = "rrn"
            private const val COLUMN_STATUS_CODE = "status_code"
            private const val COLUMN_STATUS_DESCRIPTION = "status_description"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_RECEIPT_ID TEXT PRIMARY KEY,
                $COLUMN_RRN TEXT,
                $COLUMN_STATUS_CODE TEXT,
                $COLUMN_STATUS_DESCRIPTION TEXT
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertTransaction(transaction: LocalDatabaseAuthorization?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECEIPT_ID, transaction?.receiptId)
            put(COLUMN_RRN, transaction?.rrn)
            put(COLUMN_STATUS_CODE, transaction?.statusCode)
            put(COLUMN_STATUS_DESCRIPTION, transaction?.statusDescription)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }


    fun getAllTransactions(): List<LocalDatabaseAuthorization> {
        val transactions = mutableListOf<LocalDatabaseAuthorization>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val receiptId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIPT_ID))
                val rrn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RRN))
                val statusCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_CODE))
                val statusDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_DESCRIPTION))

                val transaction = LocalDatabaseAuthorization(receiptId, rrn, statusCode, statusDescription)
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return transactions
    }

    fun getTransactionByReceiptId(receiptId: String): LocalDatabaseAuthorization? {
        val db = readableDatabase
        val selection = "$COLUMN_RECEIPT_ID = ?"
        val selectionArgs = arrayOf(receiptId)
        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var transaction: LocalDatabaseAuthorization? = null

        if (cursor.moveToFirst()) {
            val rrn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RRN))
            val statusCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_CODE))
            val statusDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS_DESCRIPTION))

            transaction = LocalDatabaseAuthorization(receiptId, rrn, statusCode, statusDescription)
        }

        cursor.close()
        db.close()

        return transaction
    }


    fun updateTransaction(transaction: LocalDatabaseAuthorization): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECEIPT_ID, transaction.receiptId)
            put(COLUMN_RRN, transaction.rrn)
            put(COLUMN_STATUS_CODE, transaction.statusCode)
            put(COLUMN_STATUS_DESCRIPTION, transaction.statusDescription)
            put("is_cancelled", if (transaction.isCancelled) 1 else 0) // Convertir el valor booleano a entero
        }

        val whereClause = "$COLUMN_RECEIPT_ID = ?"
        val whereArgs = arrayOf(transaction.receiptId)

        val rowsAffected = db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()

        return rowsAffected
    }



}