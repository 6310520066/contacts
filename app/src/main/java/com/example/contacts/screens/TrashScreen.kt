package com.example.contacts.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.contacts.R
import com.example.contacts.domain.model.ContactModel
import com.example.contacts.routing.Screen
import com.example.contacts.ui.components.AppDrawer
import com.example.contacts.ui.components.Contact
import com.example.contacts.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private const val NO_DIALOG = 1
private const val RESTORE_CONTACT_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel: MainViewModel) {

    val contactInThrash: List<ContactModel> by viewModel.contactInTrash
        .observeAsState(listOf())

    val selectedContact: List<ContactModel> by viewModel.selectedContact
        .observeAsState(listOf())

    val dialogState = rememberSaveable { mutableStateOf(NO_DIALOG) }

    val scaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedContact.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestoreContactClick = { dialogState.value = RESTORE_CONTACT_DIALOG },
                onDeleteContactClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                contact = contactInThrash,
                onContactClick = { viewModel.onContactSelected(it) },
                selectedContact = selectedContact
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog) {
                    RESTORE_CONTACT_DIALOG -> {
                        {
                            viewModel.restoreContact(selectedContact)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentlyDeleteContact(selectedContact)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialogState.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestoreContactClick: () -> Unit,
    onDeleteContactClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestoreContactClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                        contentDescription = "Restore Contact Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeleteContactClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                        contentDescription = "Delete Contact Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
@ExperimentalMaterialApi
private fun Content(
    contact: List<ContactModel>,
    onContactClick: (ContactModel) -> Unit,
    selectedContact: List<ContactModel>,
) {
    val tabs = listOf("REGULAR", "CHECKABLE")

    // Init state for selected tab
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        val filteredContact = when (selectedTab) {
            0 -> {
                contact.filter { it.isCheckedOff == null }
            }
            1 -> {
                contact.filter { it.isCheckedOff != null }
            }
            else -> throw IllegalStateException("Tab not supported - index: $selectedTab")
        }

        LazyColumn {
            items(count = filteredContact.size) { contactIndex ->
                val acontact = filteredContact[contactIndex]
                val isContactSelected = selectedContact.contains(acontact)
                Contact(
                    contact = acontact,
                    onContactClick = onContactClick,
                    isSelected = isContactSelected
                )
            }
        }
    }
}

private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_CONTACT_DIALOG -> "Restore contact"
    PERMANENTLY_DELETE_DIALOG -> "Delete contact forever"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_CONTACT_DIALOG -> "Are you sure you want to restore selected contact?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure you want to delete selected Contact permanently?"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}