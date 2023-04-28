package com.example.contacts

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contacts.routing.PhoneContactRouter
import com.example.contacts.routing.Screen
import com.example.contacts.screens.ContactScreen
import com.example.contacts.screens.SaveContactScreen
import com.example.contacts.screens.TrashScreen
import com.example.contacts.ui.theme.ContactsTheme
import com.example.contacts.ui.theme.ContactsThemeSettings
import com.example.contacts.viewmodel.MainViewModel
import com.example.contacts.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsTheme(darkTheme = ContactsThemeSettings.isDarkThemeEnabled) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
                )
                MainActivityScreen(viewModel)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (PhoneContactRouter.currentScreen) {
            is Screen.Contact -> ContactScreen(viewModel)
            is Screen.SaveContact -> SaveContactScreen(viewModel)
            is Screen.Trash -> TrashScreen(viewModel)
        }
    }
}