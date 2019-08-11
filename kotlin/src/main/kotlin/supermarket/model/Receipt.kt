package supermarket.model

class Receipt(
    val items: List<ReceiptItem>,
    val discountOffers: List<DiscountOffer>
) {
    val totalPrice: Double
        get() {
            val totalCostOfItems = items.sumByDouble { it.totalPrice }
            val totalDiscountValue = discountOffers.sumByDouble { it.discount.discountAmount }

            return totalCostOfItems - totalDiscountValue
        }
}
