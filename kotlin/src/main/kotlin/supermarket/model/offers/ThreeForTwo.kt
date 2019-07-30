package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class ThreeForTwo(
    val product: Product
) : Offer {

    override fun discountIfApplicable(
        productQuantities: ProductQuantities,
        catalog: SupermarketCatalog
    ): Discount? {
        val quantity = productQuantities.getValue(product)
        val quantityAsInt = quantity.toInt()
        val unitPrice = catalog.getUnitPrice(product)

        val minimumQuantityToApplyOffer = 3
        return if (quantityAsInt >= minimumQuantityToApplyOffer) {
            val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
            val discountAmount =
                quantity * unitPrice - (numberOfXs.toDouble() * 2.0 * unitPrice + quantityAsInt % 3 * unitPrice)
            Discount(
                products = setOf(product),
                description = "3 for 2",
                discountAmount = discountAmount
            )
        } else null
    }

    override fun applicableProducts(): Set<Product> = setOf(product)

    override fun isOfferApplicable(productQuantities: ProductQuantities): Boolean {
        return product in productQuantities && isProductQuantityEnoughToApplyOffer(productQuantities)
    }

    private fun isProductQuantityEnoughToApplyOffer(productQuantities: ProductQuantities): Boolean {
        return productQuantities.getValue(product).toInt() >= 3
    }
}
