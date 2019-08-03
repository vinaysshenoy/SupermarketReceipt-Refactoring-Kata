package dojo.supermarket.model

import org.junit.jupiter.api.Test
import strikt.api.*
import strikt.assertions.*
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit
import supermarket.model.ReceiptItem
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount

class ReceiptTest {

    @Test
    fun `receipt for cart with 0 items`() {
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
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expect {
            that(receipt.items).isEmpty()
            that(receipt.discounts).isEmpty()
        }
    }

    @Test
    fun `receipt for cart with items and discounts`() {
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
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        val expectedReceiptItems = listOf(
            ReceiptItem(product = product1, quantity = 2.0, price = 5.0, totalPrice = 10.0),
            ReceiptItem(product = product4, quantity = 7.5, price = 20.0, totalPrice = 150.0),
            ReceiptItem(product = product2, quantity = 3.0, price = 10.0, totalPrice = 30.0),
            ReceiptItem(product = product1, quantity = 2.0, price = 5.0, totalPrice = 10.0),
            ReceiptItem(product = product3, quantity = 2.0, price = 15.0, totalPrice = 30.0),
            ReceiptItem(product = product5, quantity = 3.5, price = 25.0, totalPrice = 87.5),
            ReceiptItem(product = product3, quantity = 1.0, price = 15.0, totalPrice = 15.0)
        )
        val expectedDiscounts = listOf(
            Discount(
                products = setOf(ProductQuantity(product1, 4.0)),
                description = "2 for 18.5",
                discountAmount = -17.0
            ),
            Discount(
                products = setOf(ProductQuantity(product4, 5.0)),
                description = "5 for 95.0",
                discountAmount = 15.0
            ),
            Discount(
                products = setOf(ProductQuantity(product3, 3.0)),
                description = "3 for 2",
                discountAmount = 15.0
            ),
            Discount(
                products = setOf(ProductQuantity(product5, 3.5)),
                description = "10.0% off",
                discountAmount = 8.75
            )
        )

        expect {
            that(receipt.items)
                .containsExactlyInAnyOrder(expectedReceiptItems)
            that(receipt.discounts)
                .containsExactlyInAnyOrder(expectedDiscounts)
        }
    }

    @Test
    fun `receipt for cart with items and offers not applicable to items`() {
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
        val receipt = teller.checksOutArticlesFrom(cart)


        // then
        val expectedProductItems = listOf(
            ReceiptItem(product = product1, quantity = 1.0, price = 5.0, totalPrice = 5.0),
            ReceiptItem(product = product4, quantity = 4.5, price = 20.0, totalPrice = 90.0),
            ReceiptItem(product = product2, quantity = 3.0, price = 10.0, totalPrice = 30.0),
            ReceiptItem(product = product3, quantity = 2.0, price = 15.0, totalPrice = 30.0),
            ReceiptItem(product = product5, quantity = 3.5, price = 25.0, totalPrice = 87.5)
        )

        val expectedDiscounts = listOf(
            Discount(
                products = setOf(ProductQuantity(product5, 3.5)),
                description = "10.0% off",
                discountAmount = 8.75
            )
        )

        expect {
            that(receipt.items)
                .containsExactlyInAnyOrder(expectedProductItems)
            that(receipt.discounts)
                .containsExactlyInAnyOrder(expectedDiscounts)
        }
    }
}
