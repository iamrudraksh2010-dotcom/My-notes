package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NoteDatabase
import com.example.data.NoteRepository
import com.example.ui.NoteViewModel
import com.example.ui.NoteViewModelFactory
import com.example.ui.NotesApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = NoteDatabase.getDatabase(this)
    val repository = NoteRepository(database.noteDao())
    
    setContent {
      val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(repository)
      )
      
      MyApplicationTheme {
        NotesApp(viewModel = viewModel)
      }
    }
  }
}

