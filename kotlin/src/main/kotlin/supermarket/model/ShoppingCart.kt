package supermarket.model

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
        return offers.mapNotNull { it.discountIfApplicable(accumulatedProductQuantities(), catalog) }
    }

    private fun accumulatedProductQuantities(): Map<Product, Double> {
        return items
            .groupBy { it.product }
            .map { (product, quantities) ->
                val totalQuantityForProduct = quantities.sumByDouble { it.quantity }

                product to totalQuantityForProduct
            }
            .toMap()
    }
}
