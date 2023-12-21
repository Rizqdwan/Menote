package com.example.noteapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.local.entities.NoteEntity
import com.example.noteapp.data.repo.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val notesRepo: NotesRepo) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val notes = searchQuery.flatMapLatest { query ->
        getFilteredNotes(query)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        searchQuery.emit(query)
    }

    private fun getFilteredNotes(query: String): Flow<List<NoteEntity>> {
        return notesRepo.notes.map { notes ->
            notes.filter { it.title?.contains(query, ignoreCase = true) == true }
        }
    }
}
