package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

data class TenPercentDiscount(
    val product: Product
) : Offer {

    private val discountPercent = 10.0

    override fun discountIfApplicable(
        productQuantities: Map<Product, Double>,
        catalog: SupermarketCatalog
    ): Discount? {
        val quantity = productQuantities.getValue(product)
        val unitPrice = catalog.getUnitPrice(product)
        return Discount(
            product,
            "$discountPercent% off",
            quantity * unitPrice * discountPercent / 100.0
        )
    }
}
