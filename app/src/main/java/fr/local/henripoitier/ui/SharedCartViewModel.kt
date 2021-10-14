package fr.local.henripoitier.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import fr.local.henripoitier.data.entities.BooksListServerResponseItem
import fr.local.henripoitier.data.entities.CommercialOffersServerResponse
import fr.local.henripoitier.data.repository.BooksRepository
import fr.local.henripoitier.utils.OfferType
import fr.local.henripoitier.utils.Resource
import kotlinx.coroutines.launch

class SharedCartViewModel @ViewModelInject constructor(private val booksRepository: BooksRepository) :
    ViewModel() {
    private val update = MutableLiveData(false)
    val elementsInCart: MutableLiveData<Int> = MutableLiveData(0)
    val booksMap = MutableLiveData(HashMap<BooksListServerResponseItem, Int>())
    val commercialOffers = update.switchMap { forceUpdate ->
        val result =
            MutableLiveData<Resource<CommercialOffersServerResponse>>(Resource.loading(null))
        if (forceUpdate) {
            viewModelScope.launch {
                result.value = Resource.loading(null)
                val isbns = getIsbns()
                if (isbns.isNotEmpty()) {
                    result.value = booksRepository.getCommercialOffers(isbns)
                }
            }
        }
        result
    }
    val pairPromo = MutableLiveData<Pair<CommercialOffersServerResponse.Offer?, Double>>()
    private val mutableSelectedItem = MutableLiveData<BooksListServerResponseItem>()
    val selectedItem: LiveData<BooksListServerResponseItem> get() = mutableSelectedItem

    fun selectBook(item: BooksListServerResponseItem) {
        mutableSelectedItem.value = item
    }

    fun addBook(book: BooksListServerResponseItem) {
        val map = booksMap.value ?: HashMap()
        val countAllElements = elementsInCart.value ?: 0
        elementsInCart.value = countAllElements + 1
        var count = map[book]
        if (count == null) {
            count = 1
        } else {
            count += 1
        }
        map[book] = count
        booksMap.value = map
    }

    fun removeSingleBookInstance(book: BooksListServerResponseItem) {
        val map = booksMap.value ?: HashMap()
        val countAllElements = elementsInCart.value ?: 1
        var count: Int = map[book] ?: return
        if (count > 0) {
            count -= 1
            elementsInCart.value = countAllElements - 1
        } else if (count == 0) {
            map.remove(book)
        }
        map[book] = count
        booksMap.value = map
    }

    fun deleteBook(book: BooksListServerResponseItem) {
        val map = booksMap.value ?: HashMap()
        var countAllElements = elementsInCart.value ?: 0
        val countBookInstances = map[book]
        if (countBookInstances != null) {
            countAllElements -= countBookInstances
            elementsInCart.value = countAllElements
            map.remove(book)
            booksMap.value = map
        }
    }

    fun getCommercialOffer() {
        update.value = true
    }

    private fun getIsbns(): String {
        var isbns = ""
        val map = booksMap.value ?: HashMap()
        for ((key, value) in map) {
            for (i in 1..value) {
                isbns += "${key.isbn},"
            }
        }
        if (isbns.isNotEmpty()) {
            isbns = isbns.substring(0, isbns.length - 1)
        }
        return isbns
    }

    fun calculateBestOffer(listOffers: List<CommercialOffersServerResponse.Offer?>?) {
        val totalPrice = getTotalPrice()
        if (listOffers.isNullOrEmpty()) {
            pairPromo.value = Pair(null, totalPrice)
        } else {
            val mapPromotions = HashMap<CommercialOffersServerResponse.Offer, Double>()
            for (offer in listOffers) {
                offer?.let {
                    getOfferPrice(totalPrice, it)?.let { promoPrice ->
                        mapPromotions.put(it, promoPrice)
                    }
                }
            }
            if (mapPromotions.isNotEmpty()) {
                pairPromo.value = getMinPromotion(mapPromotions)
            } else {
                pairPromo.value = Pair(null, totalPrice)
            }
        }
    }

    private fun getMinPromotion(mapPromotions: HashMap<CommercialOffersServerResponse.Offer, Double>): Pair<CommercialOffersServerResponse.Offer?, Double> {
        var min: Double = Double.MAX_VALUE
        var keyPromo: CommercialOffersServerResponse.Offer? = null
        for ((key, value) in mapPromotions) {
            if (min > value) {
                min = value
                keyPromo = key
            }
        }
        return Pair(keyPromo, min)
    }

    private fun getOfferPrice(
        totalPrice: Double,
        offer: CommercialOffersServerResponse.Offer
    ): Double? {
        var promotedPrice: Double? = null
        offer.type?.let {
            when (it) {
                OfferType.PERCENTAGE.type -> {
                    promotedPrice = totalPrice - (totalPrice * (offer.value ?: 0).toDouble() / 100)
                }
                OfferType.MINUS.type -> {
                    promotedPrice = totalPrice - (offer.value ?: 0).toDouble()
                }
                OfferType.SLICE.type -> {
                    offer.sliceValue?.let { slice ->
                        val multiplier = (totalPrice / slice).toInt()
                        offer.value?.let { value ->
                            promotedPrice = totalPrice - (multiplier * value).toDouble()
                        }
                    }
                }
                else -> Unit
            }
        }
        return promotedPrice
    }

    fun getTotalPrice(): Double {
        val map = booksMap.value ?: HashMap()
        return getTotalPriceForMap(map)
    }

    /**
     * Package-private for unit tests
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getTotalPriceForMap(map: HashMap<BooksListServerResponseItem, Int>): Double {
        var totalPrice = 0
        for ((key, value) in map) {
            key.price?.let {
                totalPrice += it * value
            }
        }
        return totalPrice.toDouble()
    }

    fun addCurrentBook() {
        selectedItem.value?.let {
            addBook(it)
        }
    }
}