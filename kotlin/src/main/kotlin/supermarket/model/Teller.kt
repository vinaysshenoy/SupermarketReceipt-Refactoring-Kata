package supermarket.model

import supermarket.model.offers.Offer

class Teller(private val catalog: SupermarketCatalog) {
    private var offers = listOf<Offer>()

    fun addOffers(vararg offersToAdd: Offer) {
        offersToAdd.forEach { offerToAdd ->
            val applicableProductsOfNewOffer = offerToAdd.applicableProducts()

            offers = offers
                .filter { offer -> offer.applicableProducts() != applicableProductsOfNewOffer }
                .plus(offerToAdd)
        }
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

        theCart.applyDiscounts(offers, catalog)
            .forEach(receipt::addDiscount)

        return receipt
    }

}
