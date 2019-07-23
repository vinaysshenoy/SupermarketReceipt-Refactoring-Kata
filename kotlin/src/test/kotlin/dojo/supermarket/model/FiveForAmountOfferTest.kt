package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import supermarket.model.*
import supermarket.model.SpecialOfferType.*

@DisplayName("five for amount offer test")
class FiveForAmountOfferTest {

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
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

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
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

        // when
        cart.addItemQuantity(product = product, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(20.0)
    }

    @DisplayName("offer applied to 1 item with quantity 3 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 3`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

        // when
        cart.addItemQuantity(product = product, quantity = 3.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(30.0)
    }

    @DisplayName("offer applied to 1 item with quantity 4 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 4`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

        // when
        cart.addItemQuantity(product = product, quantity = 4.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(40.0)
    }

    @DisplayName("offer applied to 1 item with quantity 5 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 5`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(45.0)
    }

    @DisplayName("offer applied to 1 item with quantity 6 in cart with 1 item")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 6`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 10.0)
        teller.addSpecialOffer(offerType = FiveForAmount, product = product, argument = 45.0)

        // when
        cart.addItemQuantity(product = product, quantity = 6.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(55.0)
    }

    @DisplayName("offer applied to first item with quantity 5 in cart with 2 items")
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
        teller.addSpecialOffer(offerType = FiveForAmount, product = product1, argument = 42.0)

        // when
        cart.addItemQuantity(product = product1, quantity = 5.0)
        cart.addItemQuantity(product = product2, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(142.0)
    }

    @DisplayName("offer applied to second item with quantity 5 in cart with 2 items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 8`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 20.0)
        }
        teller.addSpecialOffer(offerType = FiveForAmount, product = product2, argument = 47.0)

        // when
        cart.addItemQuantity(product = product1, quantity = 5.0)
        cart.addItemQuantity(product = product2, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(97.0)
    }

    @DisplayName("offer applied to both items with quantity 5 in cart with 2 items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 9`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 20.0)
        }
        teller.apply{
            addSpecialOffer(offerType = FiveForAmount, product = product1, argument = 43.0)
            addSpecialOffer(offerType = FiveForAmount, product = product2, argument = 84.0)
        }

        // when
        cart.addItemQuantity(product = product1, quantity = 5.0)
        cart.addItemQuantity(product = product2, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(127.0)
    }
}
