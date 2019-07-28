package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import supermarket.model.Product
import supermarket.model.ProductUnit
import supermarket.model.ShoppingCart
import supermarket.model.Teller
import supermarket.model.offers.ThreeForTwo

@DisplayName("three for two offers test")
class ThreeForTwoOfferTest {

    private val catalog = FakeCatalog()
    private val cart = ShoppingCart()
    private val teller = Teller(catalog)

    @DisplayName("offer applied to only one item with one quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 1`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))


        // when
        cart.addItemQuantity(product = product, quantity = 1.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(5.0)
    }

    @DisplayName("offer applied to only one item with two quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 2`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))


        // when
        cart.addItemQuantity(product = product, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(10.0)
    }

    @DisplayName("offer applied to only one item with three quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 3`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))

        // when
        cart.addItemQuantity(product = product, quantity = 3.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(10.0)
    }

    @DisplayName("offer applied to only one item with four quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 4`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))

        // when
        cart.addItemQuantity(product = product, quantity = 4.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(15.0)
    }

    @DisplayName("offer applied to only one item with five quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 5`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(20.0)
    }

    @DisplayName("offer applied to only one item with six quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 6`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(ThreeForTwo(product = product))

        // when
        cart.addItemQuantity(product = product, quantity = 6.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(20.0)
    }

    @DisplayName("offer applied to two items each with quantity three in cart with two items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 7`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)

        catalog.addProduct(product = product1, price = 5.0)
        catalog.addProduct(product = product2, price = 0.5)
        teller.addOffers(
            ThreeForTwo(product = product1),
            ThreeForTwo(product = product2)
        )

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 3.0)
            addItemQuantity(product = product2, quantity = 3.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(11.0)
    }

    @DisplayName("offer applied to two items each with high quantity in cart with three items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 8`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product1", productUnit)
        val product2 = Product("product2", productUnit)
        val product3 = Product("product3", productUnit)

        catalog.addProduct(product = product1, price = 5.0)
        catalog.addProduct(product = product2, price = 0.5)
        catalog.addProduct(product = product3, price = 100.0)
        teller.addOffers(
            ThreeForTwo(product = product1),
            ThreeForTwo(product = product2)
        )

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 75.0)
            addItemQuantity(product = product2, quantity = 300.0)
            addItemQuantity(product = product3, quantity = 100.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(10350.0)
    }
}
