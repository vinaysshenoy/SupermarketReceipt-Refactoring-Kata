package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.SupermarketCatalog

data class TenPercentDiscount(
    val product: Product
) : Offer {

    private val discountPercent = 10.0

    override fun discount(
        allProducts: ProductQuantities,
        catalog: SupermarketCatalog
    ): Discount {
        val quantity = allProducts.getValue(product)
        val unitPrice = catalog.getUnitPrice(product)
        return Discount(
            products = setOf(ProductQuantity(product, quantity)),
            description = "$discountPercent% off(${product.name})",
            discountAmount = quantity * unitPrice * discountPercent / 100.0
        )
    }

    override fun applicableProducts(): Set<Product> = setOf(product)

    override fun isOfferApplicable(productQuantities: ProductQuantities): Boolean {
        return product in productQuantities
    }
}
