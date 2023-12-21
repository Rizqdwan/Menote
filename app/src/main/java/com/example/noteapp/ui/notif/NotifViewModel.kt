package com.example.noteapp.ui.notif

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.local.entities.NotifEntity
import com.example.noteapp.data.repo.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotifViewModel @Inject constructor(private val notesRepo: NotesRepo) : ViewModel(){

//   val notes: Flow<List<NoteEntity>> = notesRepo.notes

   val notif: Flow<List<NotifEntity>> = notesRepo.notif


}