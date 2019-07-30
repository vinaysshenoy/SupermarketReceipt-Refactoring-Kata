package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

class XForAmount(
    val product: Product,
    val quantityForOffer: Double,
    val amount: Double
) : Offer {

    override fun discount(productQuantities: ProductQuantities, catalog: SupermarketCatalog): Discount {
        val quantity = productQuantities.getValue(product)
        val quantityAsInt = quantity.toInt()
        val minimumQuantityToApplyOffer = quantityForOffer.toInt()

        require(quantityAsInt >= minimumQuantityToApplyOffer)

        val unitPrice = catalog.getUnitPrice(product)
        val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
        val discountTotal =
            unitPrice * quantity - (amount * numberOfXs + quantityAsInt % minimumQuantityToApplyOffer * unitPrice)
        return Discount(
            products = setOf(product),
            description = "$minimumQuantityToApplyOffer for $amount",
            discountAmount = discountTotal
        )
    }

    override fun applicableProducts(): Set<Product> = setOf(product)

    override fun isOfferApplicable(productQuantities: ProductQuantities): Boolean {
        return product in productQuantities && isProductQuantityEnoughToApplyOffer(productQuantities)
    }

    private fun isProductQuantityEnoughToApplyOffer(productQuantities: ProductQuantities): Boolean {
        return productQuantities.getValue(product).toInt() >= quantityForOffer
    }
}
