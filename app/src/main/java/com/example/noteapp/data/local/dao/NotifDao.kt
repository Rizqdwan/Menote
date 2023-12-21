package com.example.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.data.local.entities.NotifEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifDao {

    @Query("SELECT * FROM notifikasi ORDER BY id DESC")
    fun getAllNotif(): Flow<List<NotifEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotif(note: NotifEntity)

}