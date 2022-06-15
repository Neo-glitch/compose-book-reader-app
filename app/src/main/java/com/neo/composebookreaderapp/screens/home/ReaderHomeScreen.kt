package com.neo.composebookreaderapp.screens.home

import android.widget.HorizontalScrollView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.components.FABContent
import com.neo.composebookreaderapp.components.ListCard
import com.neo.composebookreaderapp.components.ReaderAppBar
import com.neo.composebookreaderapp.components.TitleSection
import com.neo.composebookreaderapp.model.MBook
import com.neo.composebookreaderapp.navigation.ReaderScreens

@Composable
fun HomeScreen(navController: NavHostController) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book reader", navController = navController)
    },
        floatingActionButton = {
            FABContent {

            }
        }) {

        Surface(modifier = Modifier.fillMaxSize()) {
            HomeContent(navController)
        }

    }

}


@Composable
fun HomeContent(navController: NavHostController) {
    val listOfBooks = listOf(
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null),
        MBook(id = "gfhfh", title = "Hell no", authors = "Schmizel", notes = null)
    )
    val email = Firebase.auth.currentUser?.email
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

        ReadingRightNowArea(books = listOf(), navController = navController)

        TitleSection(label = "Reading List")

        BookListArea(listOfBooks = listOfBooks, navController = navController)



    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavHostController) {
    HorizontalScrollableComponent(listOfBooks){

    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState() // to remember scroll state of row, lazyRow has it's own way

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ){
        for (book in listOfBooks){
            ListCard(book){
                onCardPressed(it)

            }
        }
    }
}


@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavHostController) {
    ListCard()
}



