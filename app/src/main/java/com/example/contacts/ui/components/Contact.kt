package com.example.contacts.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.contacts.domain.model.ContactModel
import com.example.contacts.utils.fromHex


@ExperimentalMaterialApi
@Composable
fun Contact(
    modifier: Modifier = Modifier,
    contact : ContactModel,
    onContactClick: (ContactModel) -> Unit = {},
    onContactCheckedChange: (ContactModel) -> Unit = {},
    isSelected: Boolean,
    context: Context
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = contact.name + " ("+contact.tag+")", maxLines = 1) },
            secondaryText = {
                Text(text = contact.phoneNumber, maxLines = 1)
            },
            icon = {
                ContactIcon(
                    color = Color.fromHex(contact.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
//            trailing = {
//                if (contact.isCheckedOff != null) {
//                    Checkbox(
//                        checked = contact.isCheckedOff,
//                        onCheckedChange = { isChecked ->
//                            val newContact = contact.copy(isCheckedOff = isChecked)
//                            onContactCheckedChange.invoke(newContact)
//                        },
//                        modifier = Modifier.padding(start = 8.dp)
//                    )
//                }
//            },
//            trailing = {
//                IconButton(onClick = {}) {
//                    Icon(
//                        imageVector = Icons.Default.Call,
//                        contentDescription = "Delete Contact Button",
//                        tint = MaterialTheme.colors.onPrimary
//                    )
//                }
//            }

            trailing = {
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${contact.phoneNumber}")
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                    )
                }

            },
            modifier = Modifier.clickable {
                onContactClick.invoke(contact)
            }
        )
    }
}
