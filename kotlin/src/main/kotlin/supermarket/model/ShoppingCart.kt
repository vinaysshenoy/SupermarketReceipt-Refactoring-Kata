package supermarket.model

import java.util.*

class ShoppingCart {

    private val items = ArrayList<ProductQuantity>()
    internal var productQuantities = mutableMapOf<Product, Double>()


    internal fun getItems(): List<ProductQuantity> {
        return ArrayList(items)
    }

    internal fun addItem(product: Product) {
        this.addItemQuantity(product, 1.0)
    }

    internal fun productQuantities(): Map<Product, Double> {
        return productQuantities
    }


    fun addItemQuantity(product: Product, quantity: Double) {
        items.add(ProductQuantity(product, quantity))
        if (productQuantities.containsKey(product)) {
            productQuantities[product] = productQuantities[product]!! + quantity
        } else {
            productQuantities[product] = quantity
        }
    }

    internal fun handleOffers(receipt: Receipt, offers: Map<Product, Offer>, catalog: SupermarketCatalog) {
        for (p in productQuantities().keys) {
            val quantity = productQuantities[p]!!
            if (offers.containsKey(p)) {
                val offer = offers[p]!!
                val unitPrice = catalog.getUnitPrice(p)

                discountIfOfferApplicable(
                    offer = offer,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    product = p
                )?.let { discount ->
                    receipt.addDiscount(discount)
                }
            }
        }
    }

    private fun discountIfOfferApplicable(
        offer: Offer,
        quantity: Double,
        unitPrice: Double,
        product: Product
    ): Discount? {

        val quantityAsInt = quantity.toInt()

        return if (offer.offerType === SpecialOfferType.ThreeForTwo && quantityAsInt > 2) {
            val minimumQuantityToApplyOffer = 3
            val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
            val discountAmount =
                quantity * unitPrice - (numberOfXs.toDouble() * 2.0 * unitPrice + quantityAsInt % 3 * unitPrice)
            Discount(product, "3 for 2", discountAmount)

        } else if (offer.offerType === SpecialOfferType.TwoForAmount) {
            val minimumQuantityToApplyOffer = 2
            if (quantityAsInt >= 2) {
                val total =
                    offer.argument * quantityAsInt / minimumQuantityToApplyOffer + quantityAsInt % 2 * unitPrice
                val discountN = unitPrice * quantity - total
                Discount(product, "2 for " + offer.argument, discountN)
            } else null

        } else if (offer.offerType === SpecialOfferType.FiveForAmount && quantityAsInt >= 5) {
            val minimumQuantityToApplyOffer = 5
            val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
            val discountTotal =
                unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % 5 * unitPrice)
            Discount(product, minimumQuantityToApplyOffer.toString() + " for " + offer.argument, discountTotal)
        } else if (offer.offerType === SpecialOfferType.TenPercentDiscount) {
            Discount(product, offer.argument.toString() + "% off", quantity * unitPrice * offer.argument / 100.0)
        } else null
    }
}
