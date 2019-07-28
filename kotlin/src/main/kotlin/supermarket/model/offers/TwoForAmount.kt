package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class TwoForAmount(
    val product: Product,
    val amount: Double
) : Offer {

    override fun discountIfApplicable(
        productQuantities: Map<Product, Double>,
        catalog: SupermarketCatalog
    ): Discount? {
        val quantity = productQuantities.getValue(product)
        val quantityAsInt = quantity.toInt()
        val unitPrice = catalog.getUnitPrice(product)

        val minimumQuantityToApplyOffer = 2
        return if (quantityAsInt >= minimumQuantityToApplyOffer) {
            val total =
                amount * quantityAsInt / minimumQuantityToApplyOffer + quantityAsInt % 2 * unitPrice
            val discountN = unitPrice * quantity - total
            Discount(product, "2 for $amount", discountN)
        } else null
    }
}
