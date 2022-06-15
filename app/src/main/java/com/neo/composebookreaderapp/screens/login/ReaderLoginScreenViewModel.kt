package com.neo.composebookreaderapp.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.neo.composebookreaderapp.model.MUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ReaderLoginScreenViewModel: ViewModel() {

//    val loadingState = MutableStateFlow(LoadingState.IDLE)

    // obj for firebase authentication
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit)=
        viewModelScope.launch {
            // don't know why coroutine is used since firebase tasks run in background already
            // just following along though
            try{
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            home()   // go to hoe screen
                        } else{
                            Log.d("SignIn error", "SignIn failed : ${task.result}")
                        }

                        _loading.value = false

                    }
            }catch (e: Exception){
                Log.d("SignIn error", "SignIn failed : ${e.message}")
            }
        }

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit){
        if(_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName)
                        home()
                    } else{
                        Log.d("SignIn error", "SignIn failed : ${task.result}")
                    }
                }
        }

    }

    /**
     * adds a user info to firebase cloud firestore db
     */
    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid

        // model class can be used for this
        val user = MUser(
            id = null,
            userId = userId.toString(), displayName = displayName!!,
            avatarUrl = "", quote = "work like hell",
            profession = "Software engineer"
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

}