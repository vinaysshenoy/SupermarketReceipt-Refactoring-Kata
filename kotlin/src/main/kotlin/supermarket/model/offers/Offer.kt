package supermarket.model.offers

import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.SpecialOfferType
import supermarket.model.SupermarketCatalog

interface Offer {

    fun discountIfApplicable(productQuantities: Map<Product, Double>, catalog: SupermarketCatalog): Discount?

    companion object {
        fun create(offerType: SpecialOfferType, product: Product, argument: Double): Offer {
            return when (offerType) {
                SpecialOfferType.ThreeForTwo -> ThreeForTwo(product = product)
                SpecialOfferType.TenPercentDiscount -> TenPercentDiscount(
                    product = product
                )
                SpecialOfferType.TwoForAmount -> TwoForAmount(
                    product = product,
                    amount = argument
                )
                SpecialOfferType.FiveForAmount -> FiveForAmount(
                    product = product,
                    amount = argument
                )
            }
        }
    }
}

