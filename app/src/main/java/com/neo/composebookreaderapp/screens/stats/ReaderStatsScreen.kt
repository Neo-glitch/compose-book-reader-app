package com.neo.composebookreaderapp.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.screens.home.ReaderHomeViewModel
import com.neo.composebookreaderapp.utils.formatDate
import okhttp3.internal.format
import java.util.*


@Composable
fun StatsScreen(
    navController: NavHostController,
    homeViewModel: ReaderHomeViewModel = hiltViewModel()
) {

    var books: List<MBook>
    val currentUser = Firebase.auth.currentUser

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Stats",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ){
            navController.popBackStack()
        }
    }) {
        Surface {
            // only show books that have been read by this user
            books = if (!homeViewModel.data.value.data.isNullOrEmpty()) {
                homeViewModel.data.value.data!!.filter { book ->
                    (book.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }

            Column {

                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                    ) {

                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }

                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase(Locale.getDefault())}")
                }

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = CircleShape,
                elevation = 5.dp){

                    // books I have read
                    val readBooksList: List<MBook> = if(!homeViewModel.data.value.data.isNullOrEmpty()){
                        books.filter { book -> book.userId == currentUser!!.uid && book.finishedReading != null }
                    } else{
                        emptyList()
                    }

                    // books being read now
                    val readingBooks = books.filter {book ->
                        book.startedReading != null && book.finishedReading == null
                    }
                    
                    Column(modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")
                        
                    }
                }

                if(homeViewModel.data.value.loading == true){
                    LinearProgressIndicator()
                } else{
                    Divider()

                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)){
                        // filter books by finished ones
                        val readBooks: List<MBook> = if(!homeViewModel.data.value.data.isNullOrEmpty()){
                            homeViewModel.data.value.data!!.filter { book ->
                                book.userId == currentUser!!.uid && book.finishedReading != null
                            }
                        } else{
                            emptyList()
                        }

                        items(items = readBooks){ book ->
                            BookRowStats(book = book)
                        }
                    }
                }

            }


        }

    }

}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun BookRowStats(book: MBook) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            // if smallThumbnail empty use the hard coded imageUrl

            val imageUrl: String =
                if(book.photoUrl.isNullOrEmpty()){
                    "https://imagesvc.meredithcorp.io/v3/mm/image?url=https%3A%2F%2Fstatic.onecms.io%2Fwp-content%2Fuploads%2Fsites%2F23%2F2022%2F02%2F08%2Ffinance-books-2022.jpg"
                } else{
                    book.photoUrl.toString()
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
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)

                    if(book.rating!! >= 3){
                        Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                        Icon(imageVector = Icons.Default.GppGood, contentDescription = "Thumps up",
                        tint = Color.Green.copy(0.5f))
                    } else {
                        Box {}
                    }
                }
                Text(
                    text = "Author: ${book.authors}", overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,  // to enable breaking long lines
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Finished : ${formatDate(book.finishedReading!!)}", overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )
            }
        }

    }
}