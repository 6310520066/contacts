package com.example.contacts.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_CONTACT = listOf(
            ContactDbModel(1, "Thammasat Hospital", "029269999","Emergency",  false,false,12, false),
            ContactDbModel(2, "Police", "191","Emergency",  false,false,12, false),
            ContactDbModel(3, "Fire", "199","Emergency",  false,false,12, false),
            ContactDbModel(4, "Emergency Car", "1669","Emergency",  false,false,12, false),
        )
    }
}