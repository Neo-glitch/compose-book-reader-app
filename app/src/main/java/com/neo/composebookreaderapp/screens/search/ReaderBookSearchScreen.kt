@file:OptIn(ExperimentalComposeUiApi::class)

package com.neo.composebookreaderapp.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.neo.composebookreaderapp.components.InputField
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.model.Item
import com.neo.composebookreaderapp.model.MBook

@Preview
@Composable
fun SearchScreen(
    navController: NavHostController = NavHostController(LocalContext.current),
    viewModel: ReaderBookSearchViewModel = hiltViewModel()  // this vm is scoped to this navigation screen
) {

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Search Books",
                icon = Icons.Default.ArrowBack,
                navController = navController,
                showProfile = false
            ) {
                navController.popBackStack()
//                navController.navigate(ReaderScreens.ReaderHomeScreen.name)
            }
        }
    ) {
        Surface {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel = viewModel
                ) { query ->
                    viewModel.searchBooks(query)
                }

                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController)
            }

        }
    }

}

@Composable
fun BookList(
    navController: NavHostController,
    viewModel: ReaderBookSearchViewModel = hiltViewModel()
) {

    val listOfBooks = viewModel.list
    if(viewModel.isLoading){
        LinearProgressIndicator()
    } else{
        Log.d("BOOK LIST", "list size is: ${listOfBooks.size}")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp) // add margin each item
        ) {
            items(items = listOfBooks) { book ->
                BookRow(book, navController)
            }
        }
    }

}

@Composable
fun BookRow(book: Item, navController: NavHostController) {
    Card(modifier = Modifier
        .clickable { }
        .fillMaxWidth()
        .heightIn(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            // if smallThumbnail empty use the hard coded imageUrl
            val imageUrl: String =
                if(book.volumeInfo.imageLinks.smallThumbnail.isNullOrEmpty()){
                    "https://imagesvc.meredithcorp.io/v3/mm/image?url=https%3A%2F%2Fstatic.onecms.io%2Fwp-content%2Fuploads%2Fsites%2F23%2F2022%2F02%2F08%2Ffinance-books-2022.jpg"
                } else{
                     book.volumeInfo.imageLinks.smallThumbnail
                }

            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "book image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column() {
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Author: ${book.volumeInfo.authors}", overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}", overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "${book.volumeInfo.categories}", overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )
            }
        }

    }
}

@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    viewModel: ReaderBookSearchViewModel,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {},
) {
    Column {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(valueState = searchQueryState, labelId = "search", enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions

                onSearch.invoke(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            })
    }

}













