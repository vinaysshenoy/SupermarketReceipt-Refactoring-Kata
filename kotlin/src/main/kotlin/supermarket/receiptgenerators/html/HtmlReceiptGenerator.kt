package supermarket.receiptgenerators.html

import supermarket.model.Receipt
import supermarket.receiptgenerators.ReceiptGenerator

class HtmlReceiptGenerator: ReceiptGenerator {

    override fun generate(receipt: Receipt, optionsMap: Map<String, Any>): String {
        return """
            |<!DOCTYPE html>
            |<html>
            |   <body>
            |           <pre>
            |Total:                              0.00
            |           </pre>
            |   </body>
            |</html>
        """.trimMargin()
    }
}
