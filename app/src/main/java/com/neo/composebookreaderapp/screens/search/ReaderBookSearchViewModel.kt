package com.neo.composebookreaderapp.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.composebookreaderapp.data.DataOrException
import com.neo.composebookreaderapp.data.Resource
import com.neo.composebookreaderapp.model.Item
import com.neo.composebookreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReaderBookSearchViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {

    var list: List<Item> by mutableStateOf(listOf()) // to hold list of books(it's a state also)
    var isLoading: Boolean by mutableStateOf(true)  // to track loading(it's a state also)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("flutter")
    }

    fun searchBooks(query: String) {

        // n.b: To fix issue with setting mutable State in Dispatchers.IO are;
        // 1. to call this method in a launched effect( And avoid calling this in the init block of this VM class)
        // 2. Or set to Context to Dispatchers.Main when setting value of the mutableState using withContext(Dispatchers.Main)
        // 3. change the mutable state in viewModel to mutableState flow and use collectAsState in composable

        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) return@launch
            try {
                withContext(Dispatchers.Main){isLoading = true}
                when (val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if (list.isNotEmpty())
                            withContext(Dispatchers.Main){isLoading = false}
                        Log.d("RESPONSE", "response: ${list.size}")
                    }
                    is Resource.Error -> {
                        withContext(Dispatchers.Main){isLoading = false}
                        Log.e("ERROR", "searchBooks: Failed getting books")
                    }
                    else -> {
                        withContext(Dispatchers.Main){isLoading = false}
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){isLoading = false}
                Log.e("ERROR_IN_CATCH", "searchBooks: ${e.message.toString()}")

            }
        }
    }

}
