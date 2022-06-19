package com.neo.composebookreaderapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName


// for stuff saved to firebase firestore, val properties don't work well... best to use var
data class MBook(
    // @exclude means don't add this field to fb database, (field is also auto gen)
    // after saving a book to db and getting fb doc Id, we update this and save back to fb to update the book in server
    @Exclude var id: String? = null,
    var title: String? = null,
    var authors: String? = null,
    var notes: String? = null,
    @get:PropertyName("book_photo_url")
    @set:PropertyName("book_photo_url")
    var photoUrl: String? = null,
    var categories: String? = null,

    @get:PropertyName("published_date")  // to set property name to published_date when saving to firebase and
    @set:PropertyName("published_date")  // to serialize published_data field value from response from fb to this field
    var publishedDate: String? = null,

    var rating: Double? = null,
    var description: String? = null,

    @get:PropertyName("page_count")
    @set:PropertyName("page_count")
    var pageCount: String? = null,

    @get:PropertyName("started_reading_at")
    @set:PropertyName("started_reading_at")
    var startedReading: Timestamp? = null,

    @get:PropertyName("finished_reading_at")
    @set:PropertyName("finished_reading_at")
    var finishedReading: Timestamp? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,  // needed by backend guy for filtering user books based on userId

    @get:PropertyName("google_book_id")
    @set:PropertyName("google_book_id")
    var googleBookId: String? = null
)
