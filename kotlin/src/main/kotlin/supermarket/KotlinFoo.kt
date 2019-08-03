package supermarket

import supermarket.model.Product
import supermarket.model.ProductQuantity

typealias ProductQuantities = Map<Product, Double>

fun ProductQuantities.remove(productQuantity: ProductQuantity): ProductQuantities {
    val updatedQuantity = this
        .getValue(productQuantity.product)
        .let { quantity -> quantity - productQuantity.quantity }

    return this
        .toMutableMap()
        .apply { set(productQuantity.product, updatedQuantity) }
        .toMap()
}
