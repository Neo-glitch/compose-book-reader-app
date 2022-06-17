package com.neo.composebookreaderapp.screens.login

import android.inputmethodservice.Keyboard
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.neo.composebookreaderapp.R
import com.neo.composebookreaderapp.components.EmailInput
import com.neo.composebookreaderapp.components.PasswordInput
import com.neo.composebookreaderapp.components.ReaderLogo
import com.neo.composebookreaderapp.navigation.ReaderScreens

@ExperimentalComposeUiApi
@Composable
fun LoginScreen(navController: NavHostController,
                loginViewModel: ReaderLoginScreenViewModel = viewModel()
) {

    // if true show login form else, create account form
    val showLoginForm = rememberSaveable { mutableStateOf(true) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ReaderLogo()

            if (showLoginForm.value)
                UserForm(loading = false, isCreateAccount = false) { email, password ->
                    Log.d("Form", "Logging with $email")
                    loginViewModel.signInWithEmailAndPassword(email, password){
                        navController.navigate(route = ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            else
                UserForm(loading = false, isCreateAccount = true){email, password ->
                    loginViewModel.createUserWithEmailAndPassword(email, password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                val text = if(showLoginForm.value) "Sign up" else "Login"
                Text(text = "New User?")
                Text(text,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondaryVariant)
            }


        }

    }
}


@ExperimentalComposeUiApi
@Preview
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> }
) {
    // state persistent to config changes
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController =
        LocalSoftwareKeyboardController.current  // needed to do stuff like hiding keyboard
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    val modifier = Modifier
        .height(250.dp)
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())  // to handle scrolling in Column


    
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // shows this only on signup flow
        if(isCreateAccount)
            Text(text = stringResource(id = R.string.create_acct),
            modifier = Modifier.padding(4.dp))
        
        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequest.requestFocus() // request for focus on input using this in modifier
        })

        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = !loading, // true when loading is false
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions

                onDone(email.value.trim(), password.value.trim())
            }
        )

        SubmitButton(
            textId = if (isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }


}

@Composable
fun SubmitButton(
    textId: String, loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape
    ) {
        if (loading)
            CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(text = textId, modifier = Modifier.padding(5.dp))
    }
}






















