package dojo.supermarket.model

import org.junit.jupiter.api.Test
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

class ReceiptPrinterTest {

    @Test
    fun `receipt for cart with bundle discount`() {
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
            ThreeForTwo(product = product3),
            XForAmount(product = product1, quantityForOffer = 2.0, amount = 18.5),
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
        val printedReceipt = receiptPrinter.printReceipt(teller.checksOutArticlesFrom(cart))

        // then
        val expectedReceipt = """
                    |product 1                          10.00
                    |  5.00 * 2.000
                    |product 4                         150.00
                    |  20.00 * 7
                    |product 2                          30.00
                    |  10.00 * 3
                    |product 1                          10.00
                    |  5.00 * 2.000
                    |product 3                          30.00
                    |  15.00 * 2.000
                    |product 5                          87.50
                    |  25.00 * 3.500
                    |product 3                          15.00
                    |3 for 2(product 3)                -15.00
                    |2 for 18.5(product 1)            --17.00
                    |10.0% off(product 5)               -8.75
                    |5 for 95.0(product 4)             -15.00
                    |
                    |Total:                            310.75
                """.trimMargin()
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }
}
