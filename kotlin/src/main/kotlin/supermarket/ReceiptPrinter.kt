package supermarket

import supermarket.model.Receipt
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
                val description = discountOffer.description()

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
}
