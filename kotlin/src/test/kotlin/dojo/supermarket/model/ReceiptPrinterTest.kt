package dojo.supermarket.model

import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*
import supermarket.ReceiptPrinter
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit
import supermarket.model.Receipt
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.BundleDiscountOffer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount
import supermarket.receiptgenerators.ReceiptGenerator
import supermarket.receiptgenerators.plaintext.PlainTextReceiptGenerator

class ReceiptPrinterTest {

    private val receiptPrinter = ReceiptPrinter()

    @Test
    fun `receipt for cart with bundle discount`() {
        // given
        val format = "TXT"
        receiptPrinter.register(format, PlainTextReceiptGenerator())

        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val apples = Product("Apples", ProductUnit.Kilo)
        val peeler = Product("Peeler", ProductUnit.Each)
        val oranges = Product("Oranges", ProductUnit.Kilo)
        val vegetableOil = Product("Vegetable Oil", ProductUnit.Each)
        val onions = Product("Onions", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = apples, price = 5.0)
            addProduct(product = peeler, price = 10.0)
            addProduct(product = oranges, price = 15.0)
            addProduct(product = vegetableOil, price = 20.0)
            addProduct(product = onions, price = 25.0)
        }

        teller.addOffers(
            BundleDiscountOffer(
                bundle = setOf(
                    ProductQuantity(apples, 1.5),
                    ProductQuantity(peeler, 1.0)
                ),
                discountPercent = 15.0
            ),
            ThreeForTwo(product = oranges),
            TenPercentDiscount(product = apples),
            TenPercentDiscount(product = onions),
            XForAmount(product = vegetableOil, quantityForOffer = 5.0, amount = 95.0),
            TenPercentDiscount(product = peeler)
        )

        // when
        cart.apply {
            addItemQuantity(product = apples, quantity = 2.0)
            addItemQuantity(product = vegetableOil, quantity = 7.5)
            addItemQuantity(product = peeler, quantity = 3.0)
            addItemQuantity(product = apples, quantity = 2.0)
            addItemQuantity(product = oranges, quantity = 2.0)
            addItemQuantity(product = onions, quantity = 3.5)
            addItem(product = oranges)
        }

        // when
        val printedReceipt = receiptPrinter.printReceipt(format, teller.checksOutArticlesFrom(cart))

        // then
        val expectedReceipt = """
                    |Apples                             10.00
                    |  5.00 * 2.000
                    |Vegetable Oil                     150.00
                    |  20.00 * 7
                    |Peeler                             30.00
                    |  10.00 * 3
                    |Apples                             10.00
                    |  5.00 * 2.000
                    |Oranges                            30.00
                    |  15.00 * 2.000
                    |Onions                             87.50
                    |  25.00 * 3.500
                    |Oranges                            15.00
                    |15.00% off(Apples 1.500 + Peeler 1)-5.25
                    |3 for 2(Oranges)                  -15.00
                    |10.0% off(Apples)                  -0.50
                    |10.0% off(Onions)                  -8.75
                    |5 for 95.0(Vegetable Oil)         -15.00
                    |10.0% off(Peeler)                  -1.00
                    |
                    |Total:                            287.00
                """.trimMargin()
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }

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
