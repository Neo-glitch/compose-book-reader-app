package com.neo.composebookreaderapp.model


// for stuff saved to firebase firestore, val properties don't work well... best to use var
data class MBook(
    var id: String? = null,
    var title: String? = null,
    var authors: String? = null,
    var notes: String? = null
)
