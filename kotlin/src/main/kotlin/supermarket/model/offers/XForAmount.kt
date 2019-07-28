package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

class XForAmount(
    val product: Product,
    val quantityForOffer: Double,
    val amount: Double
) : Offer {

    override fun discountIfApplicable(productQuantities: Map<Product, Double>, catalog: SupermarketCatalog): Discount? {
        val quantity = productQuantities.getValue(product)
        val quantityAsInt = quantity.toInt()

        val minimumQuantityToApplyOffer = quantityForOffer.toInt()
        return if (quantityAsInt >= minimumQuantityToApplyOffer) {
            val unitPrice = catalog.getUnitPrice(product)
            val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
            val discountTotal =
                unitPrice * quantity - (amount * numberOfXs + quantityAsInt % minimumQuantityToApplyOffer * unitPrice)
            Discount(product, "$minimumQuantityToApplyOffer for $amount", discountTotal)
        } else null
    }

    override fun applicableProducts() = listOf(product)
}
