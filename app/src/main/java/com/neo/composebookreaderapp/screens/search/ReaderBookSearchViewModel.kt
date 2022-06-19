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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderBookSearchViewModel @Inject constructor(private val repository: BookRepository) :
ViewModel(){

    // previously used stuff
//    val listOfBooks: MutableState<DataOrException<List<Item>, Boolean, Exception>> =
//        mutableStateOf(DataOrException(null, false, Exception("")))
//
//
//    init {
//        searchBooks("messi")
//    }
//
//    fun searchBooks(query: String) {
//        //todo find way to make dispatchers.io as work with this
//        viewModelScope.launch {
//            if(query.isEmpty()) return@launch
//
//            listOfBooks.value.loading = true
//            listOfBooks.value = repository.getBooks(query)
//            if(listOfBooks.value.data.toString().isNotEmpty()
//                    ){
//                listOfBooks.value.loading = false
//                Log.d("LOAD_VM", "list of books: ${listOfBooks.value.data.toString()}")
//            }
//
//        }
//    }

    var list: List<Item> by mutableStateOf(listOf()) // to hold list of books

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("flutter")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(query.isEmpty()) return@launch

            try{
                when(val response = repository.getBooks(query)){
                    is Resource.Success -> {
                        list = response.data!!
                        Log.d("RESPONSE", "response: ${list.size}")
                    }
                    is Resource.Error -> Log.e("ERROR", "searchBooks: Failed getting books")
                    else -> {}
                }
            }catch (e: Exception){
                Log.e("ERROR_IN_CATCH", "searchBooks: ${e.message.toString()}")
            }
        }
    }

}
