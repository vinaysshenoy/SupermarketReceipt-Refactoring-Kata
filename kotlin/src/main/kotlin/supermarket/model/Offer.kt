package supermarket.model

sealed class Offer {

    abstract fun discountIfApplicable(productQuantities: Map<Product, Double>, catalog: SupermarketCatalog): Discount?

    data class ThreeForTwo(
        val product: Product
    ) : Offer() {

        override fun discountIfApplicable(
            productQuantities: Map<Product, Double>,
            catalog: SupermarketCatalog
        ): Discount? {
            val quantity = productQuantities.getValue(product)
            val quantityAsInt = quantity.toInt()
            val unitPrice = catalog.getUnitPrice(product)

            val minimumQuantityToApplyOffer = 3
            return if (quantityAsInt >= minimumQuantityToApplyOffer) {
                val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
                val discountAmount =
                    quantity * unitPrice - (numberOfXs.toDouble() * 2.0 * unitPrice + quantityAsInt % 3 * unitPrice)
                Discount(product, "3 for 2", discountAmount)
            } else null
        }
    }

    data class TenPercentDiscount(
        val product: Product
    ) : Offer() {

        private val discountPercent = 10.0

        override fun discountIfApplicable(
            productQuantities: Map<Product, Double>,
            catalog: SupermarketCatalog
        ): Discount? {
            val quantity = productQuantities.getValue(product)
            val unitPrice = catalog.getUnitPrice(product)
            return Discount(product, "$discountPercent% off", quantity * unitPrice * discountPercent / 100.0)
        }
    }

    data class TwoForAmount(
        val product: Product,
        val amount: Double
    ) : Offer() {

        override fun discountIfApplicable(
            productQuantities: Map<Product, Double>,
            catalog: SupermarketCatalog
        ): Discount? {
            val quantity = productQuantities.getValue(product)
            val quantityAsInt = quantity.toInt()
            val unitPrice = catalog.getUnitPrice(product)

            val minimumQuantityToApplyOffer = 2
            return if (quantityAsInt >= minimumQuantityToApplyOffer) {
                val total =
                    amount * quantityAsInt / minimumQuantityToApplyOffer + quantityAsInt % 2 * unitPrice
                val discountN = unitPrice * quantity - total
                Discount(product, "2 for $amount", discountN)
            } else null
        }
    }

    data class FiveForAmount(
        val product: Product,
        val amount: Double
    ) : Offer() {

        override fun discountIfApplicable(
            productQuantities: Map<Product, Double>,
            catalog: SupermarketCatalog
        ): Discount? {
            val quantity = productQuantities.getValue(product)
            val quantityAsInt = quantity.toInt()
            val unitPrice = catalog.getUnitPrice(product)

            val minimumQuantityToApplyOffer = 5
            return if (quantityAsInt >= minimumQuantityToApplyOffer) {
                val numberOfXs = quantityAsInt / minimumQuantityToApplyOffer
                val discountTotal =
                    unitPrice * quantity - (amount * numberOfXs + quantityAsInt % 5 * unitPrice)
                Discount(product, "$minimumQuantityToApplyOffer for $amount", discountTotal)
            } else null
        }
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
