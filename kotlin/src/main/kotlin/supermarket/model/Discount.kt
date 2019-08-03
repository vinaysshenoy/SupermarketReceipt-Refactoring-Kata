package supermarket.model

data class Discount(
    val products: Set<ProductQuantity>,
    val description: String,
    val discountAmount: Double
)
