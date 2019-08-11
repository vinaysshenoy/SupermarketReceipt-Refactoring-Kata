package supermarket

import supermarket.model.ProductQuantity
import supermarket.model.Receipt
import supermarket.model.offers.BundleDiscountOffer
import supermarket.model.offers.Offer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount
import java.util.*

class ReceiptPrinter @JvmOverloads constructor(private val columns: Int = 40) {

    fun printReceipt(receipt: Receipt): String {
        val result = StringBuilder()
        for (item in receipt.items) {
            val price = String.format(Locale.UK, "%.2f", item.totalPrice)
            val quantity = formatQuantity(item.product.unit, item.quantity)
            val name = item.product.name
            val unitPrice = String.format(Locale.UK, "%.2f", item.price)

            val whitespaceSize = this.columns - name.length - price.length
            var line = name + getWhitespace(whitespaceSize) + price + "\n"

            if (item.quantity != 1.0) {
                line += "  $unitPrice * $quantity\n"
            }
            result.append(line)
        }
        receipt
            .discountOffers
            .forEach { discountOffer ->
                val pricePresentation = String.format(Locale.UK, "%.2f", discountOffer.discount.discountAmount)
                val description = offerDescription(discountOffer.offer)

                result.append(description)
                result.append(getWhitespace(this.columns - 1 - description.length - pricePresentation.length))
                result.append("-")
                result.append(pricePresentation)
                result.append("\n")
            }
        result.append("\n")
        val pricePresentation = String.format(Locale.UK, "%.2f", receipt.totalPrice)
        val total = "Total: "
        val whitespace = getWhitespace(this.columns - total.length - pricePresentation.length)
        result.append(total).append(whitespace).append(pricePresentation)
        return result.toString()
    }

    private fun getWhitespace(whitespaceSize: Int): String {
        val whitespace = StringBuilder()
        for (i in 0 until whitespaceSize) {
            whitespace.append(" ")
        }
        return whitespace.toString()
    }

    private fun offerDescription(offer: Offer): String {
        return when (offer) {
            is ThreeForTwo -> generateThreeForTwoOfferDescription(offer)
            is TenPercentDiscount -> generateTenPercentDiscountDescription(offer)
            is XForAmount -> generateXForAmountOfferDescription(offer)
            is BundleDiscountOffer -> generateBundleDiscountDescription(offer)
            else -> throw IllegalArgumentException("Unknown type of offer: ${offer.javaClass.name}")
        }
    }

    private fun generateXForAmountOfferDescription(offer: XForAmount): String {
        return "${offer.quantityForOffer.toInt()} for ${offer.amount}(${offer.product.name})"
    }

    private fun generateTenPercentDiscountDescription(offer: TenPercentDiscount): String {
        return "${offer.discountPercent}% off(${offer.product.name})"
    }

    private fun generateThreeForTwoOfferDescription(offer: ThreeForTwo): String {
        return "3 for 2(${offer.product.name})"
    }

    private fun generateBundleDiscountDescription(offer: BundleDiscountOffer): String {
        return "%.02f%% off(%s)".format(offer.discountPercent, generateBundlePresentation(offer))
    }

    private fun generateBundlePresentation(offer: BundleDiscountOffer): String {
        return offer.bundle.joinToString(" + ", transform = this::presentProductQuantity)
    }

    private fun presentProductQuantity(productQuantity: ProductQuantity): String {
        val product = productQuantity.product
        val quantity = productQuantity.quantity

        return "${product.name} ${formatQuantity(product.unit, quantity)}"
    }
}
