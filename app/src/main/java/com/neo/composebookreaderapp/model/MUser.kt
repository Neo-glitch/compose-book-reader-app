package com.neo.composebookreaderapp.model

// 'MUser' to avoid having clash with firebase user obj in project
data class MUser(
    val id : String?,  // gotten from firebase by default
    val userId: String,
    val displayName: String,
    val avatarUrl: String,
    val quote: String,
    val profession: String
){

    // firebase those this by default when a class obj is passed
    // just following along with the course
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to userId,
            "display_name" to displayName,
            "quote" to quote,
            "profession" to profession,
            "avatar_url" to avatarUrl
        )
    }
}
