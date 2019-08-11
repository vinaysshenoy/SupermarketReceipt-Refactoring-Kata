package supermarket.receiptgenerators

import supermarket.model.Receipt

interface ReceiptGenerator {
    fun generate(receipt: Receipt, optionsMap: Map<String, Any>): String
}
