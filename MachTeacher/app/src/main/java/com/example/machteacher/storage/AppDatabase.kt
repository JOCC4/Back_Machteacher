package com.example.machteacher.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.machteacher.dao.SessionDao
import com.example.machteacher.entity.SessionEntity

@Database(entities = [SessionEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}
