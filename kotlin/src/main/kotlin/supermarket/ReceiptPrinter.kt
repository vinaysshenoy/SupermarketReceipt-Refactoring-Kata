package supermarket

import supermarket.model.Receipt
import supermarket.receiptgenerators.plaintext.PlainTextReceiptGenerator

class ReceiptPrinter {

    private val plainTextReceiptGenerator = PlainTextReceiptGenerator()

    fun printReceipt(receipt: Receipt, options: Map<String, Any> = emptyMap()): String {
        return plainTextReceiptGenerator.generate(receipt, options)
    }
}
