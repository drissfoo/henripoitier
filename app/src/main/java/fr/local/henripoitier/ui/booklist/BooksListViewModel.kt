package fr.local.henripoitier.ui.booklist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fr.local.henripoitier.data.entities.BooksListServerResponse
import fr.local.henripoitier.data.repository.BooksRepository
import fr.local.henripoitier.utils.Resource
import kotlinx.coroutines.launch


class BooksListViewModel @ViewModelInject constructor(private val booksRepository: BooksRepository): ViewModel() {
    private val _forceUpdate = MutableLiveData(false)
    val books: LiveData<Resource<BooksListServerResponse>> = _forceUpdate.switchMap { forceUpdate ->
        val result = MutableLiveData<Resource<BooksListServerResponse>>(Resource.loading(null))
        if (forceUpdate) {
            viewModelScope.launch {
                result.value = Resource.loading(null)
                result.value = booksRepository.getBooks()
            }
        }
        result
    }

    init {
        refreshBooks()
    }

    fun refreshBooks() {
        _forceUpdate.value = true
    }
}