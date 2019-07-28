package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

interface Offer {

    fun discountIfApplicable(productQuantities: Map<Product, Double>, catalog: SupermarketCatalog): Discount?

    fun applicableProducts(): List<Product>
}

