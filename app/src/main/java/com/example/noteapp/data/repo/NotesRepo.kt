package com.example.noteapp.data.repo

import com.example.noteapp.data.local.dao.NoteDao
import com.example.noteapp.data.local.dao.NotifDao
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.local.entities.NotifEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepo @Inject constructor(private val notesDao: NoteDao, private val notifDao: NotifDao) {

    val notes = notesDao.getAllNotes()

    val notif = notifDao.getAllNotif()

    suspend fun getNote(id: Int) = withContext(Dispatchers.IO) {
        notesDao.getSpecificNote(id)
    }

    suspend fun insertNote(note: NoteEntity, notif: NotifEntity) = withContext(Dispatchers.IO) {
        notesDao.insertNotes(note)
        notifDao.insertNotif(notif)
    }

//    suspend fun deleteNote(note: NoteEntity, notif: NotifEntity) = withContext(Dispatchers.IO) {
//        notesDao.deleteNotes(note.id)
//        notifDao.insertNotif(notif)
//    }

    suspend fun deleteNoteById(id: Int,notif: NotifEntity) = withContext(Dispatchers.IO) {
        notesDao.deleteSpecificNote(id)
        notifDao.insertNotif(notif)
    }

    suspend fun updateNotes(note: NoteEntity, notif: NotifEntity) = withContext(Dispatchers.IO) {
        notesDao.updateNotes(note)
        notifDao.insertNotif(notif)
    }
}