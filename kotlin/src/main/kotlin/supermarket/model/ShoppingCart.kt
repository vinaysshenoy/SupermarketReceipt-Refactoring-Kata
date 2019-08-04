package supermarket.model

import supermarket.ProductQuantities
import supermarket.model.offers.Offer
import supermarket.remove

class ShoppingCart {
    private val items = mutableListOf<ProductQuantity>()

    fun getItems(): List<ProductQuantity> {
        return items.toList()
    }

    fun addItem(product: Product) {
        this.addItemQuantity(product, 1.0)
    }

    fun addItemQuantity(product: Product, quantity: Double) {
        items.add(ProductQuantity(product, quantity))
    }

    fun applyDiscounts(offers: List<Offer>, catalog: SupermarketCatalog): List<Discount> {
        val productQuantities = accumulatedProductQuantities()

        return offers
            .asSequence()
            .fold(mutableListOf<Discount>() to productQuantities) { (discounts, productQuantities), offer ->
                if (offer.isOfferApplicable(productQuantities)) {
                    val discountToAdd = offer.discount(productQuantities, catalog)
                    discounts.add(discountToAdd)
                    discounts to removeFromProductQuantities(productQuantities, discountToAdd.products)
                } else {
                    discounts to productQuantities
                }
            }
            .first
            .toList()
    }

    private fun removeFromProductQuantities(
        productQuantities: ProductQuantities,
        productsFromDiscount: Set<ProductQuantity>
    ): ProductQuantities {
        return productsFromDiscount
            .fold(productQuantities) { acc, productQuantity -> acc.remove(productQuantity) }
    }

    private fun accumulatedProductQuantities(): ProductQuantities {
        return items
            .groupBy { it.product }
            .map { (product, quantities) ->
                val totalQuantityForProduct = quantities.sumByDouble { it.quantity }

                product to totalQuantityForProduct
            }
            .toMap()
    }
}
