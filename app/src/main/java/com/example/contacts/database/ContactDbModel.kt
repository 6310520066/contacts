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
//    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
//    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_CONTACT = listOf(
            ContactDbModel(1, "Bob DoSomething", "0871234567","Mobile",  1, false),
            ContactDbModel(2, "Bills Gate", "1871234567","Home",  2, false),
            ContactDbModel(3, "Pancake Kem", "2871234567","Work",  3, false),
            ContactDbModel(4, "Work tilldie", "3871234567","Mobile",  4, false),
            ContactDbModel(5, "Tom Cruise", "4871234567","Home",  5, false),
            ContactDbModel(6, "Josh Wdish", "5871234567","Work",  12, false)
        )
    }
}