package supermarket.model

data class Discount(
    val products: Set<ProductQuantity>,
    @Deprecated(
        message = "",
        replaceWith = ReplaceWith("discountOffer.description()")
    )
    val description: String,
    val discountAmount: Double
)
