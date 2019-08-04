package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.SupermarketCatalog

data class XForAmount(
    val product: Product,
    val quantityForOffer: Double,
    val amount: Double
) : Offer {

    override fun discount(allProducts: ProductQuantities, catalog: SupermarketCatalog): Discount {
        val quantity = allProducts.getValue(product)
        val quantityAsInt = quantity.toInt()
        val minimumQuantityToApplyOffer = quantityForOffer.toInt()

        val unitPrice = catalog.getUnitPrice(product)
        val numberOfXs = (quantityAsInt / minimumQuantityToApplyOffer).toDouble()
        val discountTotal =
            unitPrice * quantity - (amount * numberOfXs + quantityAsInt % minimumQuantityToApplyOffer * unitPrice)
        return Discount(
            products = setOf(ProductQuantity(product, numberOfXs * quantityForOffer)),
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
