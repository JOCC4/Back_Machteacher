package com.example.machteacher.di

import android.content.Context
import androidx.room.Room
import com.example.machteacher.db.AppDatabase // ✅ CORREGIDO: el paquete real es 'db', no 'storage'
import com.example.machteacher.dao.SessionDao
import com.example.machteacher.dao.MessageDao // ✅ opcional, si también lo usas
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "machteacher-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // ✅ DAO de sesiones
    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao {
        return database.sessionDao()
    }

    // ✅ (Opcional) DAO de mensajes si lo necesitas en Chat o ConversationScreen
    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }
}
