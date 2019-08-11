package supermarket.model

import supermarket.model.offers.Offer

data class DiscountOffer(
    val offer: Offer,
    val discount: Discount
)
