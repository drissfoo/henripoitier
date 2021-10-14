package fr.local.henripoitier.data

import fr.local.henripoitier.data.entities.BooksListServerResponse
import fr.local.henripoitier.data.entities.CommercialOffersServerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksService {
    @GET("books")
    suspend fun getBooks() : Response<BooksListServerResponse>

    @GET("books/{isbns}/commercialOffers")
    suspend fun getCommercialOffers(@Path(value = "isbns") arrayIsbns : String) : Response<CommercialOffersServerResponse>
}