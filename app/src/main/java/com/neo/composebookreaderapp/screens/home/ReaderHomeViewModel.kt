package com.neo.composebookreaderapp.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.composebookreaderapp.data.DataOrException
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ReaderHomeViewModel @Inject constructor(private val repository: FireRepository) :
    ViewModel() {

    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), false, null)
    )


    init {
        getAllBooksFromDatabase()
    }

    fun getAllBooksFromDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){data.value.loading = true}

            data.value = repository.getAllBooksFromDatabase()
            if(!data.value.data.isNullOrEmpty())
                withContext(Dispatchers.Main){data.value.loading = false}
            Log.d("GET BOOKS", "Get books from fb: ${data.value.data?.toList()?.toString()}")
        }
    }

}