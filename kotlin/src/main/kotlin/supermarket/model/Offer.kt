package supermarket.model

sealed class Offer {

    abstract val product: Product

    abstract val argument: Double

    data class ThreeForTwo(
        override val product: Product
    ) : Offer() {

        override val argument = -1.0
    }

    data class TenPercentDiscount(
        override val product: Product
    ) : Offer() {

        override val argument = 10.0
    }

    data class TwoForAmount(
        override val product: Product,
        val amount: Double
    ) : Offer() {

        override val argument = amount
    }

    data class FiveForAmount(
        override val product: Product,
        val amount: Double
    ) : Offer() {

        override val argument = amount
    }

    companion object {
        fun create(offerType: SpecialOfferType, product: Product, argument: Double): Offer {
            return when (offerType) {
                SpecialOfferType.ThreeForTwo -> ThreeForTwo(product = product)
                SpecialOfferType.TenPercentDiscount -> TenPercentDiscount(product = product)
                SpecialOfferType.TwoForAmount -> TwoForAmount(product = product, amount = argument)
                SpecialOfferType.FiveForAmount -> FiveForAmount(product = product, amount = argument)
            }
        }
    }
}
