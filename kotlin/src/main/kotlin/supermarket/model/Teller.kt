package supermarket.model

import supermarket.model.offers.*

class Teller(private val catalog: SupermarketCatalog) {
    private var offers = listOf<Offer>()

    @Deprecated(
        message = "Use the version that takes the Offer directly",
        replaceWith = ReplaceWith(expression = "addSpecialOffer()")
    )
    fun addSpecialOffer(offerType: SpecialOfferType, product: Product, argument: Double) {
        val offerToAdd = when (offerType) {
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
        addSpecialOffer(offerToAdd)
    }

    fun addSpecialOffer(offerToAdd: Offer) {
        val applicableProductsOfNewOffer = offerToAdd.applicableProducts()

        offers = offers
            .filter { offer -> offer.applicableProducts() != applicableProductsOfNewOffer }
            .plus(offerToAdd)
    }

    fun checksOutArticlesFrom(theCart: ShoppingCart): Receipt {
        val receipt = Receipt()
        val productQuantities = theCart.getItems()
        for (pq in productQuantities) {
            val p = pq.product
            val quantity = pq.quantity
            val unitPrice = this.catalog.getUnitPrice(p)
            val price = quantity * unitPrice
            receipt.addProduct(p, quantity, unitPrice, price)
        }
        theCart.handleOffers(receipt, this.offers, this.catalog)

        return receipt
    }

}
