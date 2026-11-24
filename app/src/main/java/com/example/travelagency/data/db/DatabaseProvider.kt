package com.example.travelagency.data.db

import android.content.Context
import androidx.room.Room

/** Синглтон базы с double-checked locking — по образцу DatabaseProvider из практики 08. */
object DatabaseProvider {
    @Volatile
    private var database: com.example.travelagency.data.db.AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.NAME,
            )
                .addCallback(DatabaseCallback())
                .build()
                .also { database = it }
        }
    }
}
