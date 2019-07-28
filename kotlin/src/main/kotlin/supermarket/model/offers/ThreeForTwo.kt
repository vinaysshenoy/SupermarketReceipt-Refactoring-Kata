package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class ThreeForTwo(
    val product: Product
) : Offer {

    override fun discountIfApplicable(
        productQuantities: Map<Product, Double>,
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
            Discount(product, "3 for 2", discountAmount)
        } else null
    }
}
