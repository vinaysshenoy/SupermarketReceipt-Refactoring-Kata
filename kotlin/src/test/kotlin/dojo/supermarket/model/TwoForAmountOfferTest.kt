package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import supermarket.model.*
import supermarket.model.SpecialOfferType.*

@DisplayName("two for amount offer test")
class TwoForAmountOfferTest {

    private val catalog = FakeCatalog()
    private val cart = ShoppingCart()
    private val teller = Teller(catalog)

    @DisplayName("offer applied to 1 item with quantity 1 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 1`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = TwoForAmount, product = product, argument = 17.5)

        // when
        cart.addItemQuantity(product = product, quantity = 1.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(10.0)
    }

    @DisplayName("offer applied to 1 item with quantity 2 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 2`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = TwoForAmount, product = product, argument = 17.5)

        // when
        cart.addItemQuantity(product = product, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(17.5)
    }

    @DisplayName("offer applied to 1 item with quantity 3 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 3`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = TwoForAmount, product = product, argument = 17.5)

        // when
        cart.addItemQuantity(product = product, quantity = 3.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        // FIXME: This logic is incorrect in production
        expectThat(receipt.totalPrice).isEqualTo(36.25)
    }

    @DisplayName("offer applied to 1 item with quantity 4 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 4`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = TwoForAmount, product = product, argument = 17.5)

        // when
        cart.addItemQuantity(product = product, quantity = 4.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(35.0)
    }

    @DisplayName("offer applied to first item with quantity 2 in cart with 2 items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 5`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 20.0)
        }
        teller.addSpecialOffer(offerType = TwoForAmount, product = product1, argument = 16.0)

        // when
        cart.addItemQuantity(product = product1, quantity = 2.0)
        cart.addItemQuantity(product = product2, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(56.0)
    }

    @DisplayName("offer applied to second item with quantity 2 in cart with 2 items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 6`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 20.0)
        }
        teller.addSpecialOffer(offerType = TwoForAmount, product = product2, argument = 35.0)

        // when
        cart.addItemQuantity(product = product1, quantity = 2.0)
        cart.addItemQuantity(product = product2, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(55.0)
    }

    @DisplayName("offer applied to both items with quantity 2 in cart with 2 items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 7`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 20.0)
        }
        teller.apply{
            addSpecialOffer(offerType = TwoForAmount, product = product1, argument = 16.0)
            addSpecialOffer(offerType = TwoForAmount, product = product2, argument = 35.0)
        }

        // when
        cart.addItemQuantity(product = product1, quantity = 2.0)
        cart.addItemQuantity(product = product2, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(51.0)
    }
}
