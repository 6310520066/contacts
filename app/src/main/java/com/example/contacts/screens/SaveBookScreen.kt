package com.example.contacts.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.contacts.R
import com.example.contacts.domain.model.ContactModel
import com.example.contacts.domain.model.ColorModel
import com.example.contacts.domain.model.NEW_CONTACT_ID
import com.example.contacts.routing.PhoneContactRouter
import com.example.contacts.routing.Screen
import com.example.contacts.ui.components.ContactIcon
import com.example.contacts.utils.fromHex
import com.example.contacts.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun SaveContactScreen(viewModel: MainViewModel) {
    val contactEntry by viewModel.contactEntry.observeAsState(ContactModel())

    val colors: List<ColorModel> by viewModel.colors.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveContactToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    val openDialog = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhoneContactRouter.navigateTo(Screen.Contact)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = contactEntry.id != NEW_CONTACT_ID
            SaveContactTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { PhoneContactRouter.navigateTo(Screen.Contact) },
                onSaveContactClick = {
                    if (contactEntry.name == "" || contactEntry.phoneNumber == "" || contactEntry.tag == ""){
                        alertText = "Please provide data (Data can't be empty)"
                        openDialog.value = true
                    }else if(!contactEntry.phoneNumber.isDigitsOnly()){
                        alertText = "Phone number should contains only digits"
                        openDialog.value = true
                    }else{
                    viewModel.saveContact(contactEntry) }
                                  },
                onOpenColorPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeleteContactClick = {
                    moveContactToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                ColorPicker(
                    colors = colors,
                    onColorSelect = { color ->
                        viewModel.onContactEntryChange(contactEntry.copy(color = color))
                    }
                )
            }
        ) {
            SaveContactContent(
                contact = contactEntry,
//                expand = mExpanded,
//                groupToSelect = groupToSelect,
//                selectedGroup = selectedGroup,
//                selectTextFieldSize = selectTextFieldSize,
                onContactChange = { updateContactEntry ->
                    viewModel.onContactEntryChange(updateContactEntry)
                }
            )
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Invalid input")
                },
                text = {
                    Column() {
                        Text(alertText)
                    }
                },
                buttons = {
                    Row(
                        modifier = Modifier.padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { openDialog.value = false }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            )
        }

        if (moveContactToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    moveContactToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move this Contact to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this Contact to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.moveContactToTrash(contactEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        moveContactToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
fun SaveContactTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveContactClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteContactClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Contact",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveContactClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Contact Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_color_lens_24),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeleteContactClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Contact Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SaveContactContent(
//    expand: MutableState<Boolean>,
//    groupToSelect: List<String>,
//    selectedGroup: MutableState<String>,
//    selectTextFieldSize: MutableState<Size>,
    contact: ContactModel,
    onContactChange: (ContactModel) -> Unit
) {
//    val icon = if (expand.value)
//        Icons.Filled.KeyboardArrowUp
//    else
//        Icons.Filled.KeyboardArrowDown


    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Name",
            text = contact.name,
            onTextChange = { newName ->
                onContactChange.invoke(contact.copy(name = newName))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone number",
            text = contact.phoneNumber,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onTextChange = { newPNumber ->
                onContactChange.invoke(contact.copy(phoneNumber = newPNumber))
            }
        )

//        OutlinedTextField(
//            readOnly = true,
//            value = selectedGroup.value,
//            onValueChange = {
//                selectedGroup.value = it
//                onContactChange.invoke(contact.copy(tag = it))
//                            },
//            modifier = Modifier
//                .heightIn(max = 240.dp)
//                .padding(top = 16.dp)
//                .onGloballyPositioned { coordinates ->
//                    // This value is used to assign to
//                    // the DropDown the same width
//                    selectTextFieldSize.value = coordinates.size.toSize()
//                },
//
//            label = {Text("Type of contact")},
//            trailingIcon = {
//                Icon(icon,"contentDescription",
//                    Modifier.clickable { expand.value = !expand.value })
//            }
//        )
//
//        DropdownMenu(expanded = expand.value
//            ,onDismissRequest = { expand.value = false }
//            ,modifier = Modifier
//            .heightIn(max = 240.dp)
//            .padding(top = 16.dp)
//            .width(with(LocalDensity.current){selectTextFieldSize.value.width.toDp()}),
//            //properties = true,
//        ) {
//            groupToSelect.forEach{label -> DropdownMenuItem(onClick = { selectedGroup.value = label; expand.value=false })
//                { Text(text = label)
//                }
//            }
//        }
        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Tag",
            text = contact.tag,
            onTextChange = { newTag ->
                onContactChange.invoke(contact.copy(tag = newTag))
            }
        )

        val canBeCheckedOff: Boolean = contact.isCheckedOff != null

        ContactCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null

                onContactChange.invoke(contact.copy(isCheckedOff = isCheckedOff))
            }
        )

        PickedColor(color = contact.color)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text,autoCorrect = false),
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}


@Composable
private fun ContactCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can contact be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedColor(color: ColorModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        ContactIcon(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        ContactIcon(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ColorItemPreview() {
    ColorItem(ColorModel.DEFAULT) {}
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT
        )
    ) { }
}

@Preview
@Composable
fun PickedColorPreview() {
    PickedColor(ColorModel.DEFAULT)
}