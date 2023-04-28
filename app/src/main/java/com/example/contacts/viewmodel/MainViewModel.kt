package com.example.contacts.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.database.AppDatabase
import com.example.contacts.database.DbMapper
import com.example.contacts.database.Repository
import com.example.contacts.domain.model.ContactModel
import com.example.contacts.domain.model.ColorModel
import com.example.contacts.routing.PhoneContactRouter
import com.example.contacts.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(application: Application) : ViewModel() {
    val contactNotInTrash: LiveData<List<ContactModel>> by lazy {
        repository.getAllContactNotInTrash()
    }

    private var _contactEntry = MutableLiveData(ContactModel())

    val contactEntry: LiveData<ContactModel> = _contactEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val contactInTrash by lazy { repository.getAllContactInTrash() }

    private var _selectedContact = MutableLiveData<List<ContactModel>>(listOf())

    val selectedContact: LiveData<List<ContactModel>> = _selectedContact

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.contactDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewContactClick() {
        _contactEntry.value = ContactModel()
        PhoneContactRouter.navigateTo(Screen.SaveContact)
    }

    fun onContactClick(contact: ContactModel) {
        _contactEntry.value = contact
        PhoneContactRouter.navigateTo(Screen.SaveContact)
    }

    fun onContactCheckedChange(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertContact(contact)
        }
    }

    fun onContactSelected(contact: ContactModel) {
        _selectedContact.value = _selectedContact.value!!.toMutableList().apply {
            if (contains(contact)) {
                remove(contact)
            } else {
                add(contact)
            }
        }
    }

    fun restoreContact(contact: List<ContactModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreContactFromTrash(contact.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContact.value = listOf()
            }
        }
    }

    fun permanentlyDeleteContact(contact: List<ContactModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteContact(contact.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContact.value = listOf()
            }
        }
    }

    fun onContactEntryChange(contact: ContactModel) {
        _contactEntry.value = contact
    }

    fun saveContact(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            if(contact.name == "" || contact.phoneNumber == "" || contact.tag == ""){

                println("Invalid input found")

                PhoneContactRouter.navigateTo(Screen.SaveContact)
//                _contactEntry.value = ContactModel()

            }else {
                repository.insertContact(contact)
                withContext(Dispatchers.Main) {
                    PhoneContactRouter.navigateTo(Screen.Contact)

                    _contactEntry.value = ContactModel()
                }
            }

        }
    }


    fun moveContactToTrash(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveContactToTrash(contact.id)

            withContext(Dispatchers.Main) {
                PhoneContactRouter.navigateTo(Screen.Contact)
            }
        }
    }
}