package dojo.supermarket.model

import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*
import supermarket.ReceiptPrinter
import supermarket.model.Receipt
import supermarket.receiptgenerators.ReceiptGenerator

class ReceiptPrinterTest {

    private val receiptPrinter = ReceiptPrinter()

    @Test
    fun `trying to generate a receipt with an unknown format must throw an error`() {
        // given
        val receipt = Receipt(
            items = emptyList(),
            discountOffers = emptyList()
        )
        val format = "UNKNOWN_FORMAT"

        // when
        val expectAction = expectCatching { receiptPrinter.printReceipt(format, receipt) }

        // then
        expectAction
            .failed()
            .isEqualTo(ReceiptPrinter.UnknownFormatException(format))
    }

    @Test
    fun `the registered receipt generator must be used for the specific format`() {
        class MockReceiptGenerator(private val generated: String) : ReceiptGenerator {
            override fun generate(receipt: Receipt, optionsMap: Map<String, Any>): String {
                return generated
            }
        }

        // given
        val receipt = Receipt(
            items = emptyList(),
            discountOffers = emptyList()
        )
        val format1 = "FORMAT_1"
        val format2 = "FORMAT_2"
        receiptPrinter.register(format2, MockReceiptGenerator("Receipt 2"))
        receiptPrinter.register(format1, MockReceiptGenerator("Receipt 1"))

        // when
        val printedReceiptForFormat1 = receiptPrinter.printReceipt(format1, receipt)
        val printedReceiptForFormat2 = receiptPrinter.printReceipt(format2, receipt)

        // then
        expect {
            that(printedReceiptForFormat1).isEqualTo("Receipt 1")
            that(printedReceiptForFormat2).isEqualTo("Receipt 2")
        }
    }
}
