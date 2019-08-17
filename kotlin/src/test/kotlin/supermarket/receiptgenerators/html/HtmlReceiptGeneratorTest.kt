package supermarket.receiptgenerators.html

import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*
import supermarket.model.Receipt

class HtmlReceiptGeneratorTest {

    @Test
    fun `receipt for a cart with no items`() {
        // given
        val receipt = Receipt(items = emptyList(), discountOffers = emptyList())
        val receiptGenerator = HtmlReceiptGenerator()

        // when
        val printedReceipt = receiptGenerator.generate(receipt, emptyMap())

        // then
        val expected = """
            |<!DOCTYPE html>
            |<html>
            |   <body>
            |           <pre>
            |Total:                              0.00
            |           </pre>
            |   </body>
            |</html>
        """.trimMargin()
        expectThat(printedReceipt).isEqualTo(expected)
    }
}
