package com.example.machteacher.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.machteacher.dao.MessageDao
import com.example.machteacher.dao.SessionDao
import com.example.machteacher.entity.MessageEntity
import com.example.machteacher.entity.SessionEntity

@Database(
    entities = [MessageEntity::class, SessionEntity::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun sessionDao(): SessionDao


}