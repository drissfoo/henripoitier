package fr.publicis.henripoitier.data.entities


import com.google.gson.annotations.SerializedName

class BooksListServerResponse : ArrayList<BooksListServerResponseItem>()

data class BooksListServerResponseItem(
    @SerializedName("isbn")
    val isbn: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("cover")
    val cover: String?,
    @SerializedName("synopsis")
    val synopsis: List<String?>?
)