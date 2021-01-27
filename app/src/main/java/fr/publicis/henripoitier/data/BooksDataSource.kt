package fr.publicis.henripoitier.data

import javax.inject.Inject

open class BooksDataSource @Inject constructor(private val booksService: BooksService) :
    BaseDataSource() {

    suspend fun getBooks() = getResult { booksService.getBooks() }
    suspend fun getCommercialOffers(isbns : String) = getResult { booksService.getCommercialOffers(isbns) }
}