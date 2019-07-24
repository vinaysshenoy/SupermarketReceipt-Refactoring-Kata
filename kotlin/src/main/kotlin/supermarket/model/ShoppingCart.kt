package supermarket.model

import supermarket.model.Offer.*
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
        for (product in productQuantities().keys) {
            val quantity = productQuantities.getValue(product)
            if (offers.containsKey(product)) {
                val offer = offers.getValue(product)
                val unitPrice = catalog.getUnitPrice(product)

                discountIfOfferApplicable(
                    offer = offer,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    product = product
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

        return when (offer) {
            is ThreeForTwo -> {
                val minimumQuantityToApplyOffer = 3
                if (quantityAsInt >= minimumQuantityToApplyOffer) {
                    val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
                    val discountAmount =
                        quantity * unitPrice - (numberOfXs.toDouble() * 2.0 * unitPrice + quantityAsInt % 3 * unitPrice)
                    Discount(product, "3 for 2", discountAmount)
                } else null
            }
            is TwoForAmount -> {
                val minimumQuantityToApplyOffer = 2
                if (quantityAsInt >= minimumQuantityToApplyOffer) {
                    val total =
                        offer.argument * quantityAsInt / minimumQuantityToApplyOffer + quantityAsInt % 2 * unitPrice
                    val discountN = unitPrice * quantity - total
                    Discount(product, "2 for " + offer.argument, discountN)
                } else null

            }
            is FiveForAmount -> {
                val minimumQuantityToApplyOffer = 5
                if (quantityAsInt >= minimumQuantityToApplyOffer) {
                    val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
                    val discountTotal =
                        unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % 5 * unitPrice)
                    Discount(product, minimumQuantityToApplyOffer.toString() + " for " + offer.argument, discountTotal)
                } else null
            }
            is TenPercentDiscount -> {
                Discount(product, offer.argument.toString() + "% off", quantity * unitPrice * offer.argument / 100.0)
            }
        }
    }
}
