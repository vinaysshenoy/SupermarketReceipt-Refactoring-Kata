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
        val result = StringBuilder()

        val receiptItemsSection = receipt
            .items
            .flatMap(this::generateReceiptItemSectionLines)
            .joinToString(separator = "\n")

        result.append(receiptItemsSection)

        if (receiptItemsSection.isNotBlank()) {
            result.append("\n")
        }

        receipt
            .discountOffers
            .map(this::generateDiscountItemSection)
            .forEach { result.append(it) }

        result.append("\n")

        val totalPriceSection = generateReceiptSummarySection(receipt)
        result.append(totalPriceSection)
        return result.toString()
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

    private fun generateDiscountItemSection(discountOffer: DiscountOffer): String {
        val pricePresentation = String.format(Locale.UK, "-%.2f", discountOffer.discount.discountAmount)
        val description = offerDescription(discountOffer.offer)
        val whitespace = getWhitespace(this.columns - description.length - pricePresentation.length)
        return "$description$whitespace$pricePresentation\n"
    }

    private fun generateReceiptSummarySection(receipt: Receipt): String {
        val pricePresentation = String.format(Locale.UK, "%.2f", receipt.totalPrice)
        val total = "Total: "
        val whitespace = getWhitespace(this.columns - total.length - pricePresentation.length)
        return "Total: $whitespace$pricePresentation"
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

    private fun formatQuantity(unit: ProductUnit, quantity: Double): String {
        return when (unit) {
            ProductUnit.Each -> String.format(Locale.UK, "%x", quantity.toInt())
            ProductUnit.Kilo -> String.format(Locale.UK, "%.3f", quantity)
        }
    }
}
