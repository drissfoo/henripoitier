package fr.publicis.henripoitier.data.entities


import com.google.gson.annotations.SerializedName

data class CommercialOffersServerResponse(
    @SerializedName("offers")
    val offers: List<Offer?>?
) {
    data class Offer(
        @SerializedName("type")
        val type: String?,
        @SerializedName("value")
        val value: Int?,
        @SerializedName("sliceValue")
        val sliceValue: Int?
    )
}