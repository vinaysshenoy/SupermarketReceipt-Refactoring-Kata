package supermarket.model

import supermarket.ProductQuantities
import supermarket.model.offers.Offer

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
            .filter { offer -> offer.isOfferApplicable(productQuantities) }
            .fold(mutableListOf<Discount>() to productQuantities) { (discounts, productQuantities), offer ->
                discounts.add(offer.discount(productQuantities, catalog))
                discounts to productQuantities
            }
            .first
            .toList()
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
