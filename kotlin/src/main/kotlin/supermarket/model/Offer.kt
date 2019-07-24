package supermarket.model

class Offer private constructor(val offerType: SpecialOfferType, val product: Product, val argument: Double) {

    companion object {
        fun create(offerType: SpecialOfferType, product: Product, argument: Double): Offer {
            return Offer(offerType, product, argument)
        }
    }
}
