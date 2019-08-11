package supermarket

import supermarket.model.DiscountOffer
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit
import supermarket.model.Receipt
import supermarket.model.ReceiptItem
import supermarket.model.offers.BundleDiscountOffer
import supermarket.model.offers.Offer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount
import java.util.*

class ReceiptPrinter @JvmOverloads constructor(private val columns: Int = 40) {

    fun printReceipt(receipt: Receipt): String {

        return listOf(
            generateReceiptItemSection(receipt),
            generateDiscountItemSection(receipt),
            generateReceiptSummarySection(receipt)
        )
            .filter(String::isNotBlank)
            .joinToString("\n")
    }

    private fun generateReceiptItemSection(receipt: Receipt): String {
        return receipt
            .items
            .flatMap(this::generateReceiptItemSectionLines)
            .joinToString(separator = "\n")
    }

    private fun generateDiscountItemSection(receipt: Receipt): String {
        return receipt
            .discountOffers
            .joinToString(separator = "\n", transform = this::generateDiscountItemLine)
    }

    private fun generateReceiptItemSectionLines(item: ReceiptItem): List<String> {
        return listOfNotNull(
            receiptItemPrimaryLine(item),
            receiptItemSecondaryLine(item)
        )
    }

    private fun receiptItemPrimaryLine(item: ReceiptItem): String {
        val price = String.format(Locale.UK, "%.2f", item.totalPrice)
        val name = item.product.name
        val whitespaceSize = this.columns - name.length - price.length
        return "$name${getWhitespace(whitespaceSize)}$price"
    }

    private fun receiptItemSecondaryLine(item: ReceiptItem): String? {
        return item
            .takeIf { it.quantity > 1.0 }
            ?.let { receiptItem ->
                val unitPrice = String.format(Locale.UK, "%.2f", receiptItem.price)
                val quantity = formatQuantity(receiptItem.product.unit, receiptItem.quantity)
                "  $unitPrice * $quantity"
            }
    }

    private fun generateDiscountItemLine(discountOffer: DiscountOffer): String {
        val pricePresentation = String.format(Locale.UK, "-%.2f", discountOffer.discount.discountAmount)
        val description = offerDescription(discountOffer.offer)
        val whitespace = getWhitespace(this.columns - description.length - pricePresentation.length)
        return "$description$whitespace$pricePresentation"
    }

    private fun generateReceiptSummarySection(receipt: Receipt): String {
        val pricePresentation = String.format(Locale.UK, "%.2f", receipt.totalPrice)
        val total = "Total: "
        val whitespace = getWhitespace(this.columns - total.length - pricePresentation.length)
        return "\nTotal: $whitespace$pricePresentation"
    }

    private fun getWhitespace(whitespaceSize: Int): String {
        return when {
            whitespaceSize > 0 -> " ".repeat(whitespaceSize)
            else -> ""
        }
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

    private fun formatQuantity(unit: ProductUnit, quantity: Double): String {
        return when (unit) {
            ProductUnit.Each -> String.format(Locale.UK, "%x", quantity.toInt())
            ProductUnit.Kilo -> String.format(Locale.UK, "%.3f", quantity)
        }
    }
}
