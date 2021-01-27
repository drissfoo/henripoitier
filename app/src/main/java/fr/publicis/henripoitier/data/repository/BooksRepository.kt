package fr.publicis.henripoitier.data.repository

import fr.publicis.henripoitier.data.BooksDataSource
import javax.inject.Inject

open class BooksRepository @Inject constructor(private val booksDataSource: BooksDataSource) {

    open suspend fun getBooks() = booksDataSource.getBooks()
    open suspend fun getCommercialOffers(isbns: String) = booksDataSource.getCommercialOffers(isbns)
}