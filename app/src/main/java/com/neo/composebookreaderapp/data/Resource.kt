package com.neo.composebookreaderapp.data


/**
 * used to wrap response gotten from retrofit,
 * alt to DataOrExceptionClass but seems more complex to use
 */
sealed class Resource<T> (
    val data: T? = null,     // used to denote api result or loading state(boolean)
    val message: String? = null  // for error message
        ){

    class Success<T> (data: T): Resource<T>(data)  // handles success
    class Error<T>(message: String?, data: T? = null): Resource<T>(data, message)  // handles error
    class Loading<T>(data: T? = null): Resource<T>(data)   // handles loading
}