package supermarket.model

import supermarket.formatQuantity
import supermarket.model.offers.BundleDiscountOffer
import supermarket.model.offers.Offer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount

data class DiscountOffer(
    val offer: Offer,
    val discount: Discount
) {
    fun description(): String {
        return when (offer) {
            is ThreeForTwo -> generateThreeForTwoOfferDescription(offer)
            is TenPercentDiscount -> generateTenPercentDiscountDescription(offer)
            is XForAmount -> generateXForAmountOfferDescription(offer)
            is BundleDiscountOffer -> generateBundleDiscountDescription(offer)
            else -> throw IllegalArgumentException("Unknown type of offer: ${offer.javaClass.name}")
        }
    }

    private fun generateXForAmountOfferDescription(offer: XForAmount): String {
        return "${offer.quantityForOffer.toInt()} for ${offer.amount}(${offer.product.name})"
    }

    private fun generateTenPercentDiscountDescription(offer: TenPercentDiscount): String {
        return "${offer.discountPercent}% off(${offer.product.name})"
    }

    private fun generateThreeForTwoOfferDescription(offer: ThreeForTwo): String {
        return "3 for 2(${offer.product.name})"
    }

    private fun generateBundleDiscountDescription(offer: BundleDiscountOffer): String {
        return "%.02f%% off(%s)".format(offer.discountPercent, generateBundlePresentation(offer))
    }

    private fun generateBundlePresentation(offer: BundleDiscountOffer): String {
        return offer.bundle.joinToString(" + ", transform = this::presentProductQuantity)
    }

    private fun presentProductQuantity(productQuantity: ProductQuantity): String {
        val product = productQuantity.product
        val quantity = productQuantity.quantity

        return "${product.name} ${formatQuantity(product.unit, quantity)}"
    }
}
