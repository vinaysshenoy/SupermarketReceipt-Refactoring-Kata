package dojo.supermarket.model

import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.failed
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import supermarket.model.Product
import supermarket.model.ProductUnit
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount

class SupermarketTest {

    private val catalog = FakeCatalog()
    private val cart = ShoppingCart()
    private val teller = Teller(catalog)

    @Test
    fun `total price for product unit of type Each must be calculated`() {
        // given
        val product1 = Product("product 1", ProductUnit.Each)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.apply {
            addProduct(product = product1, price = 100.0)
            addProduct(product = product2, price = 50.0)
        }

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product2, quantity = 3.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(350.0)
    }

    @Test
    fun `total price for product unit of type Kilo must be calculated`() {
        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = product1, price = 5.0)
            addProduct(product = product2, price = 0.75)
        }

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product2, quantity = 3.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(12.25)
    }

    @Test
    fun `total price for product unit of all types must be calculated`() {
        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 0.5)
        }

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 0.5)
            addItemQuantity(product = product2, quantity = 3.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(6.5)
    }

    @Test
    fun `total price for cart with no items must be 0`() {
        // given
        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 0.5)
        }

        // when
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(0.0)
    }

    @Test
    fun `products not in catalog added to cart`() {
        // given
        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.addProduct(product = product1, price = 10.0)

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 1.0)
            addItemQuantity(product = product2, quantity = 1.0)
        }

        // then
        expectCatching { teller.checksOutArticlesFrom(cart) }
            .failed()
            .isA<NullPointerException>()
    }

    @Test
    fun `multiple products and discounts added to cart`() {
        // given
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
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product4, quantity = 7.5)
            addItemQuantity(product = product2, quantity = 3.0)
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product3, quantity = 2.0)
            addItemQuantity(product = product5, quantity = 3.5)
            addItem(product = product3)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(310.75)
    }
}
