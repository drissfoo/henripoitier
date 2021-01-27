package fr.publicis.henripoitier.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fr.publicis.henripoitier.data.BooksDataSource
import fr.publicis.henripoitier.data.entities.BooksListServerResponseItem
import fr.publicis.henripoitier.data.entities.CommercialOffersServerResponse
import fr.publicis.henripoitier.data.repository.FakeBooksRepository
import fr.publicis.henripoitier.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SharedCartViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeRepo: FakeBooksRepository

    @Before
    fun setUp() {
        val dataSource = Mockito.mock(BooksDataSource::class.java)
        fakeRepo = FakeBooksRepository(dataSource)
    }

    @Test
    fun selectBook() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val book = BooksListServerResponseItem(
                "isbn1",
                "title1",
                10,
                "",
                listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.selectBook(book)
        assertThat(viewModel.selectedItem.value?.isbn?:"", `is`(book.isbn))
    }

    @Test
    fun addBook() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val book = BooksListServerResponseItem(
            "isbn1",
            "title1",
            10,
            "",
            listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.addBook(book)
        assertThat(viewModel.booksMap.getOrAwaitValue().size, `is`(1))
    }

    @Test
    fun removeSingleBookInstance() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val book = BooksListServerResponseItem(
                "isbn1",
                "title1",
                10,
                "",
                listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        assertThat(viewModel.booksMap.getOrAwaitValue().size, `is`(1))
        assertThat(viewModel.booksMap.getOrAwaitValue()?.get(book) ?: -1, `is`(4))
        viewModel.removeSingleBookInstance(book)
        assertThat(viewModel.booksMap.getOrAwaitValue()?.get(book) ?: -1, `is`(3))
    }

    @Test
    fun deleteBook() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val book = BooksListServerResponseItem(
            "isbn1",
            "title1",
            10,
            "",
            listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.addBook(book)
        viewModel.deleteBook(book)
        assert(viewModel.booksMap.getOrAwaitValue()[book]==null)
        assertThat(viewModel.booksMap.getOrAwaitValue().size, `is`(0))
    }

    @Test
    fun getCommercialOffer() {
        var data: List<CommercialOffersServerResponse.Offer?>
        runBlocking {
            data = fakeRepo.getCommercialOffers("").data?.offers ?: listOf()
        }
        assertThat(data.size, `is`(3))
    }

    @Test
    fun calculateBestOffer() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val percentage = CommercialOffersServerResponse.Offer("percentage", 5, null)
        val minus = CommercialOffersServerResponse.Offer("minus", 15, null)
        val slice = CommercialOffersServerResponse.Offer("slice", 12, 30)
        val data: List<CommercialOffersServerResponse.Offer?> = listOf(
                percentage,
                minus,
                slice
        )
        val book = BooksListServerResponseItem(
                "isbn1",
                "title1",
                10,
                "",
                listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.calculateBestOffer(data)
        assertThat(viewModel.pairPromo.getOrAwaitValue().second, `is`(25.toDouble()))
        assertThat(viewModel.pairPromo.getOrAwaitValue().first, `is`(minus))
    }

    @Test
    fun getTotalPriceForMap() {
        val viewModel = SharedCartViewModel(fakeRepo)
        val book = BooksListServerResponseItem(
                "isbn1",
                "title1",
                10,
                "",
                listOf("desc1-1", "desc1-2", "desc1-3")
        )
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        viewModel.addBook(book)
        assertThat(viewModel.getTotalPriceForMap(viewModel.booksMap.getOrAwaitValue()), `is`(40.toDouble()))
    }
}