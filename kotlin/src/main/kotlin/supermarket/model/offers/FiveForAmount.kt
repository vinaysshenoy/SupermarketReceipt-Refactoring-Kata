package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

@Deprecated(
    message = "XForAmount should be used instead",
    replaceWith = ReplaceWith(
        expression = "XForAmount(product, 5.0, amount)",
        imports = ["supermarket.model.offers.XForAmount"]
    )
)
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

    override fun applicableProducts() = listOf(product)
}
