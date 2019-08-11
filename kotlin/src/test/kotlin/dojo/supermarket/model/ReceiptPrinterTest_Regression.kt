package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.*
import strikt.assertions.*
import supermarket.ReceiptPrinter
import supermarket.model.Product
import supermarket.model.ProductUnit
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount
import java.util.stream.Stream

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReceiptPrinterTest_Regression {

    @DisplayName("receipt for cart with 0 items")
    @ParameterizedTest(name = "for {0} columns")
    @MethodSource("params for test 1")
    fun `test 1`(
        columns: Int,
        expectedReceipt: String
    ) {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 0.5)
        }

        // when
        val receiptPrinter = ReceiptPrinter()
        val printedReceipt = receiptPrinter.printReceipt(
            teller.checksOutArticlesFrom(cart),
            mapOf("columns" to columns)
        )

        // then
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }

    @Suppress("Unused")
    private fun `params for test 1`(): Stream<Arguments> {
        fun testCase(
            columns: Int,
            expectedReceipt: String
        ): Arguments {
            return Arguments.of(columns, expectedReceipt.trimMargin())
        }

        return Stream.of(
            testCase(
                columns = 20,
                expectedReceipt = """
                    |
                    |Total:          0.00
                """
            ),
            testCase(
                columns = 40,
                expectedReceipt = """
                    |
                    |Total:                              0.00
                """
            ),
            testCase(
                columns = 60,
                expectedReceipt = """
                    |
                    |Total:                                                  0.00
                """
            )
        )
    }

    @DisplayName("receipt for cart with items and discounts")
    @ParameterizedTest(name = "for {0} columns")
    @MethodSource("params for test 2")
    fun `test 2`(
        columns: Int,
        expectedReceipt: String
    ) {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product that has a really, really, really long name", ProductUnit.Each)
        val product3 = Product("product 3", ProductUnit.Kilo)
        val product4 = Product("product 4", ProductUnit.Each)
        val product5 = Product("product 5", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = product1, price = 5.0)
            addProduct(product = product2, price = 10.0)
            addProduct(product = product3, price = 15.0)
            addProduct(product = product4, price = 20.0)
            addProduct(product = product5, price = 25.0)
        }

        teller.addOffers(
            ThreeForTwo(product = product3),
            XForAmount(product = product1, quantityForOffer = 2.0, amount = 8.5),
            TenPercentDiscount(product = product5),
            XForAmount(product = product4, quantityForOffer = 5.0, amount = 95.0)
        )

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product4, quantity = 7.5)
            addItemQuantity(product = product2, quantity = 3.0)
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product3, quantity = 2.0)
            addItemQuantity(product = product5, quantity = 3.5)
            addItem(product = product3)
        }

        // when
        val receiptPrinter = ReceiptPrinter()
        val printedReceipt = receiptPrinter.printReceipt(
            teller.checksOutArticlesFrom(cart),
            mapOf("columns" to columns)
        )

        // then
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }

    @Suppress("Unused")
    private fun `params for test 2`(): Stream<Arguments> {
        fun testCase(
            columns: Int,
            expectedReceipt: String
        ): Arguments {
            return Arguments.of(columns, expectedReceipt.trimMargin())
        }

        return Stream.of(
            testCase(
                columns = 20,
                expectedReceipt = """
                    |product 1      10.00
                    |  5.00 * 2.000
                    |product 4     150.00
                    |  20.00 * 7
                    |product that has a really, really, really long name30.00
                    |  10.00 * 3
                    |product 1      10.00
                    |  5.00 * 2.000
                    |product 3      30.00
                    |  15.00 * 2.000
                    |product 5      87.50
                    |  25.00 * 3.500
                    |product 3      15.00
                    |3 for 2(product 3)-15.00
                    |2 for 8.5(product 1)-3.00
                    |10.0% off(product 5)-8.75
                    |5 for 95.0(product 4)-15.00
                    |
                    |Total:        290.75
                """
            ),
            testCase(
                columns = 40,
                expectedReceipt = """
                    |product 1                          10.00
                    |  5.00 * 2.000
                    |product 4                         150.00
                    |  20.00 * 7
                    |product that has a really, really, really long name30.00
                    |  10.00 * 3
                    |product 1                          10.00
                    |  5.00 * 2.000
                    |product 3                          30.00
                    |  15.00 * 2.000
                    |product 5                          87.50
                    |  25.00 * 3.500
                    |product 3                          15.00
                    |3 for 2(product 3)                -15.00
                    |2 for 8.5(product 1)               -3.00
                    |10.0% off(product 5)               -8.75
                    |5 for 95.0(product 4)             -15.00
                    |
                    |Total:                            290.75
                """
            ),
            testCase(
                columns = 60,
                expectedReceipt = """
                    |product 1                                              10.00
                    |  5.00 * 2.000
                    |product 4                                             150.00
                    |  20.00 * 7
                    |product that has a really, really, really long name    30.00
                    |  10.00 * 3
                    |product 1                                              10.00
                    |  5.00 * 2.000
                    |product 3                                              30.00
                    |  15.00 * 2.000
                    |product 5                                              87.50
                    |  25.00 * 3.500
                    |product 3                                              15.00
                    |3 for 2(product 3)                                    -15.00
                    |2 for 8.5(product 1)                                   -3.00
                    |10.0% off(product 5)                                   -8.75
                    |5 for 95.0(product 4)                                 -15.00
                    |
                    |Total:                                                290.75
                """
            )
        )
    }

    @DisplayName("receipt for cart with items and offers not applicable to items")
    @Test
    fun `test 3`() {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)
        val product3 = Product("product 3", ProductUnit.Kilo)
        val product4 = Product("product 4", ProductUnit.Each)
        val product5 = Product("product 5", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = product1, price = 5.0)
            addProduct(product = product2, price = 10.0)
            addProduct(product = product3, price = 15.0)
            addProduct(product = product4, price = 20.0)
            addProduct(product = product5, price = 25.0)
        }

        teller.addOffers(
            ThreeForTwo(product3),
            XForAmount(product1, 2.0, 18.5),
            TenPercentDiscount(product5),
            XForAmount(product4, 5.0, 95.0)
        )

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 1.0)
            addItemQuantity(product = product4, quantity = 4.5)
            addItemQuantity(product = product2, quantity = 3.0)
            addItemQuantity(product = product3, quantity = 2.0)
            addItemQuantity(product = product5, quantity = 3.5)
        }

        // when
        val receiptPrinter = ReceiptPrinter()
        val printedReceipt = receiptPrinter.printReceipt(teller.checksOutArticlesFrom(cart), mapOf("columns" to 40))

        // then
        val expectedReceipt = """
            |product 1                           5.00
            |product 4                          90.00
            |  20.00 * 4
            |product 2                          30.00
            |  10.00 * 3
            |product 3                          30.00
            |  15.00 * 2.000
            |product 5                          87.50
            |  25.00 * 3.500
            |10.0% off(product 5)               -8.75
            |
            |Total:                            233.75
        """.trimMargin()
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }
}
