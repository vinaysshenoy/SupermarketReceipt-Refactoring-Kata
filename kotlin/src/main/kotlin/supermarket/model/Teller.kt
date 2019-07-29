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
        val receiptItems = theCart
            .getItems()
            .map { (product, quantity) ->
                val unitPrice = catalog.getUnitPrice(product)
                ReceiptItem(product, quantity, unitPrice, unitPrice * quantity)
            }

        val discounts = theCart.applyDiscounts(offers, catalog)

        return Receipt(items = receiptItems, discounts = discounts)
    }
}
