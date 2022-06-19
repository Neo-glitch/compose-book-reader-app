package com.neo.composebookreaderapp.di

import com.neo.composebookreaderapp.network.BooksApi
import com.neo.composebookreaderapp.repository.BookRepository
import com.neo.composebookreaderapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBookApi(): BooksApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    // really no need for this, just following course
    @Singleton
    @Provides
    fun provideBookRepository(api : BooksApi) = BookRepository(api)


}