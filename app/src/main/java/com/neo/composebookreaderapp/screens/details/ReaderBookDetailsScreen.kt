package com.neo.composebookreaderapp.screens.details

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.components.RoundedButton
import com.neo.composebookreaderapp.data.Resource
import com.neo.composebookreaderapp.model.Item
import com.neo.composebookreaderapp.model.MBook

@Composable
fun BookDetailsScreen(
    navController: NavHostController,
    bookId: String,
    viewModel: ReaderBookDetailsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Book Details",
                icon = Icons.Default.ArrowBack,
                showProfile = false,
                navController = navController
            ) {
                navController.popBackStack()
            }
        }
    ) {

        Surface(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // get the result as a state and use ".value" to get the Resource value
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        LinearProgressIndicator()
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Loading...")
                    }
                } else {
                    ShowDetails(bookInfo, navController)
                }

            }

        }
    }
}

@Composable
fun ShowDetails(bookInfo: Resource<Item>, navController: NavHostController) {

    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id
    val context = LocalContext.current
    val showProgress = remember { mutableStateOf(false) }

    // image Card
    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape,
        elevation = 4.dp
    ) {
        Image(
            painter = rememberImagePainter(data = bookData?.imageLinks?.thumbnail),
            contentDescription = "Book Image",
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )
    }

    Text(
        text = bookData!!.title, style = MaterialTheme.typography.h6,
        overflow = TextOverflow.Ellipsis, maxLines = 7
    )

    Text("Authors: ${bookData.authors}")
    Text("Page Count: ${bookData.pageCount}")
    Text(
        "Categories: ${bookData.categories}",
        style = MaterialTheme.typography.subtitle1,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
    )
    Text("Published: ${bookData.publishedDate}", style = MaterialTheme.typography.subtitle1)

    Spacer(modifier = Modifier.height(5.dp))

    // description text free of html tag from server text
    val cleanDescription =
        HtmlCompat.fromHtml(bookData.description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

    val localDims = LocalContext.current.resources.displayMetrics  // gets display metrics of screen
    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .padding(4.dp),
        shape = RectangleShape,
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        // since we aren't display a list done this way
        // but could have used a Column composable and add vertical scrolling feature
        LazyColumn(modifier = Modifier.padding(3.dp)) {
            item {
                Text(text = cleanDescription)
            }
        }

    }

    // Button
    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if(showProgress.value){
           CircularProgressIndicator()
        } else{
            RoundedButton(label = "Save") {
                showProgress.value = true
                // save book to firestore
                val book = MBook(
                    title = bookData.title,
                    authors = bookData.authors.toString(),
                    description = bookData.description,
                    categories = bookData.categories.toString(),
                    notes = "",
                    photoUrl = bookData.publishedDate,
                    pageCount = bookData.pageCount.toString(),
                    rating = 0.0,
                    googleBookId = googleBookId,
                    userId = Firebase.auth.uid.toString()
                )
                saveToFirebase(book, navController, context){
                    showProgress.value = false
                }
            }
            Spacer(modifier = Modifier.width(25.dp))
            RoundedButton(label = "Cancel") {
                // cancel and go back
                navController.popBackStack()
            }
        }
    }


}


fun saveToFirebase(
    book: MBook,
    navController: NavHostController,
    current: Context,
    onSaveFailed: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book).addOnSuccessListener { documentRef ->
            val docId = documentRef.id
            // update the id Field in this doc
            dbCollection.document(docId).update(hashMapOf("id" to docId) as Map<String, String>)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(current, "Saved notes successfully", Toast.LENGTH_SHORT)
                            .show()
                        navController.popBackStack()
                    }
                }
        }.addOnFailureListener {
            onSaveFailed.invoke()
            Toast.makeText(current, "Error saving notes", Toast.LENGTH_LONG).show()
        }
    } else {
        onSaveFailed.invoke()
    }

}
