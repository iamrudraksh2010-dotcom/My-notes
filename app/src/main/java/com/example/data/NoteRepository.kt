package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun getNoteById(id: Int): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    suspend fun insert(note: Note): Long {
        return noteDao.insertNote(note)
    }

    suspend fun deleteById(id: Int) {
        noteDao.deleteNoteById(id)
    }
}
