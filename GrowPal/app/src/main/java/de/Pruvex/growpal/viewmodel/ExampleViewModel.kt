package de.Pruvex.growpal.viewmodel

import androidx.lifecycle.ViewModel
import de.Pruvex.growpal.data.ExampleRepository

class ExampleViewModel(private val repository: ExampleRepository = ExampleRepository()) : ViewModel() {
    fun getWelcomeMessage(): String = repository.getWelcomeMessage()
}
