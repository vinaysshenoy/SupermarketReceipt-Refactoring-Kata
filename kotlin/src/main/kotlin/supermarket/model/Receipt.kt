package supermarket.model

class Receipt(
    val items: List<ReceiptItem>,
    val discounts: List<Discount>
) {

    val totalPrice: Double
        get() {
            var total = 0.0
            for (item in this.items) {
                total += item.totalPrice
            }
            for (discount in this.discounts) {
                total -= discount.discountAmount
            }
            return total
        }
}
