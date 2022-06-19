package com.neo.composebookreaderapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.composebookreaderapp.data.Resource
import com.neo.composebookreaderapp.model.Item
import com.neo.composebookreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderBookDetailsViewModel @Inject constructor(private val repository: BookRepository)
    : ViewModel() {

        suspend fun getBookInfo(bookId: String): Resource<Item> {
            return repository.getBookInfo(bookId)
        }
}