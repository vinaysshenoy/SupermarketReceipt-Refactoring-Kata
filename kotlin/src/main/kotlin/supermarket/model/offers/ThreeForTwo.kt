package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class ThreeForTwo(
    val product: Product
) : Offer {

    override fun discount(
        productQuantities: ProductQuantities,
        catalog: SupermarketCatalog
    ): Discount {
        val minimumQuantityToApplyOffer = 3
        val quantity = productQuantities.getValue(product)
        val quantityAsInt = quantity.toInt()

        require(quantityAsInt >= minimumQuantityToApplyOffer)

        val unitPrice = catalog.getUnitPrice(product)

        val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
        val discountAmount =
            quantity * unitPrice - (numberOfXs.toDouble() * 2.0 * unitPrice + quantityAsInt % 3 * unitPrice)
        return Discount(
            products = setOf(product),
            description = "3 for 2",
            discountAmount = discountAmount
        )
    }

    override fun applicableProducts(): Set<Product> = setOf(product)

    override fun isOfferApplicable(productQuantities: ProductQuantities): Boolean {
        return product in productQuantities && isProductQuantityEnoughToApplyOffer(productQuantities)
    }

    private fun isProductQuantityEnoughToApplyOffer(productQuantities: ProductQuantities): Boolean {
        return productQuantities.getValue(product).toInt() >= 3
    }
}
