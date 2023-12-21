package com.example.noteapp.data.local

import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteapp.data.local.dao.NoteDao
import com.example.noteapp.data.local.dao.NotifDao
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.local.entities.NotifEntity


@Database(
    entities = [NoteEntity::class, NotifEntity::class],
    version = 1,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
abstract class NotesDataBase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract  fun notifDao() : NotifDao

}
