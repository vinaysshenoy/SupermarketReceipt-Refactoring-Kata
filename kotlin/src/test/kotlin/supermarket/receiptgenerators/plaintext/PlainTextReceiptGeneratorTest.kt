package supermarket.receiptgenerators.plaintext

import dojo.supermarket.model.FakeCatalog
import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit.*
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.BundleDiscountOffer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount

class PlainTextReceiptGeneratorTest {

    @Test
    fun `receipt for cart with bundle discount`() {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val apples = Product("Apples", Kilo)
        val peeler = Product("Peeler", Each)
        val oranges = Product("Oranges", Kilo)
        val vegetableOil = Product("Vegetable Oil", Each)
        val onions = Product("Onions", Kilo)
        val fantasticBeasts = Product("Fantastic Beasts And Where To Find Them, by Joanne Kathleen Rowling", Each)

        catalog.apply {
            addProduct(product = apples, price = 5.0)
            addProduct(product = peeler, price = 10.0)
            addProduct(product = oranges, price = 15.0)
            addProduct(product = vegetableOil, price = 20.0)
            addProduct(product = onions, price = 25.0)
            addProduct(product = fantasticBeasts, price = 100.0)
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
            addItem(product = fantasticBeasts)
        }

        val receiptGenerator = PlainTextReceiptGenerator()

        // when
        val printedReceipt = receiptGenerator.generate(teller.checksOutArticlesFrom(cart), emptyMap())

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
                    |Fantastic Beasts And Where To Find Them, by Joanne Kathleen Rowling100.00
                    |15.00% off(Apples 1.500 + Peeler 1)-5.25
                    |3 for 2(Oranges)                  -15.00
                    |10.0% off(Apples)                  -0.50
                    |10.0% off(Onions)                  -8.75
                    |5 for 95.0(Vegetable Oil)         -15.00
                    |10.0% off(Peeler)                  -1.00
                    |
                    |Total:                            387.00
                """.trimMargin()
        expectThat(printedReceipt).isEqualTo(expectedReceipt)
    }
}
