package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SupermarketCatalog

interface Offer {

    fun discount(allProducts: ProductQuantities, catalog: SupermarketCatalog): Discount

    fun applicableProducts(): Set<Product>

    fun isOfferApplicable(productQuantities: ProductQuantities): Boolean
}

