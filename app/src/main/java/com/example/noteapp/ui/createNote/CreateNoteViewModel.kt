package com.example.noteapp.ui.createNote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.local.entities.NotifEntity
import com.example.noteapp.data.repo.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val notesRepo: NotesRepo) : ViewModel() {

    val noteId = MutableStateFlow<Int?>(null)

    val note = noteId.flatMapLatest {
        val note = it?.let { notesRepo.getNote(it) }
        flowOf(note)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setNoteId(id:Int) = viewModelScope.launch { noteId.emit(id) }


    suspend fun updateNote(noteEntity: NoteEntity, notifEntity: NotifEntity) = notesRepo.updateNotes(noteEntity,notifEntity)

    suspend fun saveNote(noteEntity: NoteEntity, notifEntity: NotifEntity) = notesRepo.insertNote(noteEntity, notifEntity)

    suspend fun deleteNote(notifEntity: NotifEntity) = noteId.value?.let {
        println("Deleting note with ID: $it")
        notesRepo.deleteNoteById(it, notifEntity)
    }

//    suspend fun deleteNote(notifEntity: NotifEntity): Boolean {
//        return noteId.value?.let {
//            try {
//                println("Deleting note with ID: $it")
//                // Assuming that deleteNoteById is a suspend function and returns Unit
//                notesRepo.deleteNoteById(it, notifEntity)
//                true // Deletion successful
//            } catch (e: Exception) {
//                // Handle exceptions, log, etc.
//                false // Deletion failed
//            }
//        } ?: false
//    }
}