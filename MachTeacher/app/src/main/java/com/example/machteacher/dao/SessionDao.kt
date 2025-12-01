package com.example.machteacher.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.machteacher.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    // 🔹 Estudiante
    @Query("SELECT * FROM sessions WHERE studentId = :studentId ORDER BY dateTime ASC")
    fun getAllSessions(studentId: Long): Flow<List<SessionEntity>>

    // 🔹 Mentor (NECESARIO PARA QUE FUNCIONE)
    @Query("SELECT * FROM sessions WHERE mentorId = :mentorId ORDER BY dateTime ASC")
    fun getAllSessionsAsMentor(mentorId: Long): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<SessionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(entity: SessionEntity)

    @Query("DELETE FROM sessions")
    suspend fun clearAll()

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<SessionEntity?>

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
