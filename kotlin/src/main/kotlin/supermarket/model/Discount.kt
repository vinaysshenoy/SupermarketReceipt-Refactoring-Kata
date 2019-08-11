package supermarket.model

data class Discount(
    val products: Set<ProductQuantity>,
    val discountAmount: Double
)
