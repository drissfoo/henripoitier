package fr.publicis.henripoitier.data.repository

import fr.publicis.henripoitier.data.BooksDataSource
import fr.publicis.henripoitier.data.entities.BooksListServerResponse
import fr.publicis.henripoitier.data.entities.BooksListServerResponseItem
import fr.publicis.henripoitier.data.entities.CommercialOffersServerResponse
import fr.publicis.henripoitier.utils.Resource

class FakeBooksRepository(booksDataSource: BooksDataSource) : BooksRepository(booksDataSource) {

    private val simulateError: Boolean = false

    override suspend fun getBooks(): Resource<BooksListServerResponse> {
        return if (simulateError) {
            Resource.error("Appel webservice échoué pour la raison: Simulated error")
        } else {
            Resource.success(getData())
        }
    }

    private fun getData(): BooksListServerResponse {
        val data = BooksListServerResponse()
        data.addAll(
            listOf(
                BooksListServerResponseItem(
                    "isbn1",
                    "title1",
                    10,
                    "",
                    listOf("desc1-1", "desc1-2", "desc1-3")
                ),
                BooksListServerResponseItem(
                    "isbn2",
                    "title2",
                    20,
                    "",
                    listOf("desc2-1", "desc2-2", "desc2-3")
                ),
                BooksListServerResponseItem(
                    "isbn3",
                    "title3",
                    30,
                    "",
                    listOf("desc3-1", "desc3-2", "desc3-3")
                )
            )
        )
        return data
    }

    override suspend fun getCommercialOffers(isbns: String): Resource<CommercialOffersServerResponse> {
        return if (simulateError) {
            Resource.error("Appel webservice échoué pour la raison: Simulated error")
        } else {
            Resource.success(getCommercialOffersData())
        }
    }

    private fun getCommercialOffersData(): CommercialOffersServerResponse {
        return CommercialOffersServerResponse(
            listOf(
                CommercialOffersServerResponse.Offer("percentage", 5, null),
                CommercialOffersServerResponse.Offer("minus", 15, null),
                CommercialOffersServerResponse.Offer("slice", 12, 30)
            )
        )
    }
}