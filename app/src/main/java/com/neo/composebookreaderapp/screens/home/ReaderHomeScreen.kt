package com.neo.composebookreaderapp.screens.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.components.FABContent
import com.neo.composebookreaderapp.components.ListCard
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.components.TitleSection
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.navigation.ReaderScreens

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: ReaderHomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    Scaffold(topBar = {
        ReaderAppBar(title = "Book reader", navController = navController)
    },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            HomeContent(navController, homeViewModel)
        }

    }

}


@Composable
fun HomeContent(
    navController: NavHostController,
    homeViewModel: ReaderHomeViewModel
) {
//    val listOfBooks = listOf(
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
//        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null)
//    )

    var listOfBooks = emptyList<MBook>()
    val currentUser = Firebase.auth.currentUser

    if (!homeViewModel.data.value.data.isNullOrEmpty()) {

        listOfBooks = homeViewModel.data.value.data!!.toList()
            .filter { book ->
                // to get book belonging to current user
                book.userId!! == currentUser?.uid.toString()
            }

        Log.d("GET BOOKS", "${listOfBooks}")
    }

    val email = currentUser?.email
    val currentUserName = email?.substringBefore("@") ?: "N/A"


    Column(
        Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(Modifier.align(Alignment.Start)) {
            TitleSection(label = "Your reading\nactivity right now..")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = currentUserName,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.overline,
                    textAlign = TextAlign.Center,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis  // for clipping too long text with ...
                )

                Divider()
            }
        }

        ReadingRightNowArea(books = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")

        BookListArea(listOfBooks = listOfBooks, navController = navController)


    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavHostController) {
    HorizontalScrollableComponent(listOfBooks) {
        navController.navigate("${ReaderScreens.UpdateScreen.name}/${it}")
    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, onCardPressed: (String) -> Unit) {
    val scrollState =
        rememberScrollState() // to remember scroll state of row, lazyRow has it's own way

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        for (book in listOfBooks) {
            ListCard(book) {
                onCardPressed(it)
            }
        }
    }
}


@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavHostController) {
    HorizontalScrollableComponent(listOfBooks = books){
//        navController.navigate("${ReaderScreens.UpdateScreen.name}/${it}")
    }
}



