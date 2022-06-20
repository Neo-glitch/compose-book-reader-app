package com.neo.composebookreaderapp.screens.update

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.data.DataOrException
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.screens.home.ReaderHomeViewModel

@Composable
fun BookUpdateScreen(
    navController: NavHostController,
    bookItemId: String,
    viewModel: ReaderHomeViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Update Book",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        )
    }) {

        // gets state
        val bookInfo = produceState<DataOrException<List<MBook>, Boolean, Exception>>(
            initialValue = DataOrException(data = emptyList(), true, null)
        ){
            value = viewModel.data.value
            viewModel.getAllBooksFromDatabase()
        }.value

        Log.d("BOOK_INFO", bookInfo.toString())

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Column(modifier = Modifier.padding(top = 3.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
                if(bookInfo.loading == true){
                    LinearProgressIndicator()
                } else{
                    Text(text = bookInfo.data?.get(0)?.title.toString())
                    
                }


            }

        }
    }

}