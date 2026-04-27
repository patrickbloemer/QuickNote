package com.quicknote.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: Long = 0,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("pt", "BR"))
        return sdf.format(Date(createdAt))
    }

    fun dayHeader(): String {
        val now = Calendar.getInstance()
        val noteDate = Calendar.getInstance().apply { timeInMillis = createdAt }

        return when {
            isSameDay(now, noteDate) -> "Hoje"
            isYesterday(now, noteDate) -> "Ontem"
            else -> {
                val sdf = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR"))
                sdf.format(Date(createdAt))
            }
        }
    }

    private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(today: Calendar, other: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, other)
    }
}

class NoteDatabase(context: Context) : SQLiteOpenHelper(context, "quicknote.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }

    fun insertNote(content: String): Note {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("content", content)
            put("created_at", now)
        }
        val id = writableDatabase.insert("notes", null, values)
        return Note(id, content, now)
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val cursor = readableDatabase.query(
            "notes", null, null, null, null, null, "created_at DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                notes.add(
                    Note(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return notes
    }

    fun deleteNote(id: Long) {
        writableDatabase.delete("notes", "id = ?", arrayOf(id.toString()))
    }
}
