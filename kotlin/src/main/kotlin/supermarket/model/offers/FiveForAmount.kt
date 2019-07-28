package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class FiveForAmount(
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

        val minimumQuantityToApplyOffer = 5
        return if (quantityAsInt >= minimumQuantityToApplyOffer) {
            val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
            val discountTotal =
                unitPrice * quantity - (amount * numberOfXs + quantityAsInt % 5 * unitPrice)
            Discount(product, "$minimumQuantityToApplyOffer for $amount", discountTotal)
        } else null
    }
}
