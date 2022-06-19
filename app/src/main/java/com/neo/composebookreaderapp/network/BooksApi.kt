package com.neo.composebookreaderapp.network

import com.neo.composebookreaderapp.model.Book
import com.neo.composebookreaderapp.model.Item
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton


@Singleton
interface BooksApi {

    @GET("volumes")
    suspend fun getAllBooks(@Query("q") query: String) : Book

    // get info about a particular book
    @GET("volumes/{bookId}")
    suspend fun getBookInfo(@Path("bookId")bookId: String) : Item

}