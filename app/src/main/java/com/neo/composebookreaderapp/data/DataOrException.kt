package com.neo.composebookreaderapp.data


/**
 * T = any data type
 * Boolean = if loading or not loading
 * E = any Exception
 */
data class DataOrException<T, Boolean, E: Exception>(
    var data: T? = null,
    var loading: Boolean? = null,
    var exception: E? = null
)
