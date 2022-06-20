package com.neo.composebookreaderapp.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.neo.composebookreaderapp.data.DataOrException
import com.neo.composebookreaderapp.model.MBook
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Repo for firebase related operations
 */
class FireRepository @Inject constructor(
    private val queryBook: Query
)
{

    suspend fun getAllBooksFromDatabase(): DataOrException<List<MBook>, Boolean, Exception> {
        val dataOrException = DataOrException<List<MBook>, Boolean, Exception>()

        try{
            dataOrException.loading = true
            // ret a list mapped to MBook obj
            dataOrException.data = queryBook.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MBook::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false

        }catch (e: FirebaseFirestoreException){
            dataOrException.exception = e
        }

        Log.d("REPO_BOOKS", dataOrException.data.toString())
        return dataOrException
    }
}