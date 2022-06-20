@file:OptIn(ExperimentalComposeUiApi::class)

package com.neo.composebookreaderapp.screens.update

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.R
import com.neo.composebookreaderapp.components.InputField
import com.neo.composebookreaderapp.components.RatingBar
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.components.RoundedButton
import com.neo.composebookreaderapp.data.DataOrException
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.navigation.ReaderNavigation
import com.neo.composebookreaderapp.navigation.ReaderScreens
import com.neo.composebookreaderapp.screens.home.ReaderHomeViewModel
import com.neo.composebookreaderapp.utils.formatDate

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
        ) {
            navController.popBackStack()
        }
    }) {

        val bookInfo = viewModel.data.value

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                } else if (bookInfo.data?.size!! > 0) {
                    androidx.compose.material.Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        elevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo = bookInfo, bookItemId = bookItemId)
                    }

                    ShowSimpleForm(book = bookInfo.data?.first() { mBook ->
                        mBook.googleBookId == bookItemId
                    }, navController)


                }


            }

        }
    }

}

@Composable
fun ShowSimpleForm(book: MBook?, navController: NavHostController) {
    val notesText = remember { mutableStateOf("") }
    val isStartedReading = remember { mutableStateOf(false) }
    val isFinishedReading = remember { mutableStateOf(false) }
    val ratingVal = remember { mutableStateOf(0) }

    val context = LocalContext.current

    SimpleForm(
        defaultValue = if (!book?.notes.isNullOrEmpty())
            book?.notes.toString()
        else
            "No thoughts available."
    ) { note ->
        notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        // button that seems like just a textView...we set it to enabled when start Reading value of MBook has no value
        TextButton(
            onClick = { isStartedReading.value = true },
            enabled = book?.startedReading == null
        ) {
            if (book?.startedReading == null) {
                if (!isStartedReading.value)
                    Text(text = "Start Reading")
                else
                    Text(
                        text = "Started Reading!", modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(0.5f)
                    )
            } else {
                // we have started reading the book
                Text("Started on: ${formatDate(book.startedReading!!)}")
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        TextButton(
            onClick = { isFinishedReading.value = true },
            enabled = book?.finishedReading == null
        ) {
            if (book?.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read")
                } else {
                    Text(text = "Finished Reading!")
                }
            } else {
                Text(text = "Finished on: ${formatDate(book.finishedReading!!)}")
            }
        }

    }
    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book?.rating?.toInt().let { rating ->
        RatingBar(rating = rating!!) { innerRating ->
            ratingVal.value = innerRating
        }

    }

    Spacer(modifier = Modifier.height(15.dp))

    val changedNotes = book?.notes != notesText.value // true only when it has been updated
    val changedRating = book?.rating?.toInt() != ratingVal.value
    val isFinishedTimeStamp = if (isFinishedReading.value)
        Timestamp.now() // since done reading get current time stamp
    else
        book?.finishedReading
    val isStartedTimestamp = if (isStartedReading.value) Timestamp.now() else book?.startedReading
    val bookUpdate =
        changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

    val bookToUpdate = hashMapOf(
        // keys have to be same as what is on firebase db doc
        "finished_reading_at" to isFinishedTimeStamp,
        "started_reading_at" to isStartedTimestamp,
        "rating" to ratingVal.value,
        "notes" to notesText.value
    ).toMap()
    Row {
        RoundedButton(label = "Update") {
            if (bookUpdate) {
                Firebase.firestore.collection("books")
                    .document(book?.id!!)
                    .update(bookToUpdate)
                    .addOnSuccessListener {
                        showToast(context, "Book updated successfully")
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                            popUpTo(0)
                        }
                    }
                    .addOnFailureListener {
                        showToast(context, "Error updating book")
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                            popUpTo(0)
                        }
                    }
            } else {
                // noting in the book has been updated
                showToast(
                    context,
                    "There is nothing to update the current book with, since nothing changed",
                )
            }
        }
        Spacer(modifier = Modifier.width(100.dp))

        // funny way of showing alert dialog in compose
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(
                message = stringResource(id = R.string.sure) + "\n" +
                        stringResource(id = R.string.action),
                openDialog
            ) {
                Firebase.firestore.collection("books")
                    .document(book?.id!!)
                    .delete()
                    .addOnSuccessListener {
                        openDialog.value = false
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name) {
                            popUpTo(0)
                        }
                    }
            }
        }

        RoundedButton(label = "Delete") {
            openDialog.value = true
        }
    }


}

@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            // to make dialog uncancellable, can just leave this empty
            openDialog.value = false
        }, title = { Text(text = "Delete Book") },
            text = { Text(text = message) },
            buttons = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onYesPressed.invoke() }) {
                        Text(text = "Yes")
                    }

                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "No")
                    }

                }
            })
    }

}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
) {
    Column() {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) {
            textFieldValue.value.trim().isNotEmpty()
        }

        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue, labelId = "Enter your thoughts", enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions

                onSearch.invoke(textFieldValue.value.trim())
                keyboardController?.hide()
            })


    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
    Row {
        Spacer(modifier = Modifier.width(43.dp))

        if (bookInfo.data != null) {
            Column(
                Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})

            }
        }

    }
}

@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(
        Modifier
            .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable {

            }, elevation = 8.dp
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberImagePainter(data = book.photoUrl),
                contentDescription = "book image",
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )

            Column() {
                Text(
                    text = book.title.toString(), style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 0.dp)
                )


                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                )

            }
        }


    }
}






























