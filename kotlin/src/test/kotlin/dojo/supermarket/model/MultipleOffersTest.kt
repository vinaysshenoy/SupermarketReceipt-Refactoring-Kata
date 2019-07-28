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
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.TwoForAmount
import supermarket.model.offers.XForAmount

@DisplayName("multiple offers applied to same item test")
class MultipleOffersTest {

    private val catalog = FakeCatalog()
    private val cart = ShoppingCart()
    private val teller = Teller(catalog)

    @DisplayName("two for amount and five for amount offers applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 1`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            TwoForAmount(product = product, amount = 8.0),
            XForAmount(product = product, quantityForOffer = 5.0, amount = 40.0)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(40.0)
    }

    @DisplayName("five for amount and two for amount offers applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 2`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            XForAmount(product = product, quantityForOffer = 5.0, amount = 40.0),
            TwoForAmount(product = product, amount = 8.0)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(25.0)
    }

    @DisplayName("two for amount and ten percent discount offers applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 3`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            TwoForAmount(product = product, amount = 8.0),
            TenPercentDiscount(product = product)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(9.0)
    }

    @DisplayName("ten percent discount offers and two for amount applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 4`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            TenPercentDiscount(product = product),
            TwoForAmount(product = product, amount = 8.0)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 2.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(8.0)
    }

    @DisplayName("three for two, ten percent discount, two for amount, five for amount applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 5`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            ThreeForTwo(product = product),
            XForAmount(product = product, quantityForOffer = 5.0, amount = 10.0),
            TwoForAmount(product = product, amount = 8.0),
            XForAmount(product = product, quantityForOffer = 5.0, amount = 20.0)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(20.0)
    }

    @DisplayName("five for amount, three for two, ten percent discount, two for amount applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 6`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            XForAmount(product = product, quantityForOffer = 5.0, amount = 20.0),
            ThreeForTwo(product = product),
            TenPercentDiscount(product = product),
            TwoForAmount(product = product, amount = 8.0)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(25.0)
    }

    @DisplayName("two for amount, five for amount, three for two, ten percent discount applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 7`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            TwoForAmount(product = product, amount = 8.0),
            XForAmount(product = product, quantityForOffer = 5.0, amount = 20.0),
            ThreeForTwo(product = product),
            TenPercentDiscount(product = product)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(22.5)
    }

    @DisplayName("ten percent discount, two for amount, five for amount, three for two applied to the same item in cart")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 8`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.0)
        teller.addOffers(
            TenPercentDiscount(product = product),
            TwoForAmount(product = product, amount = 8.0),
            XForAmount(product = product, quantityForOffer = 5.0, amount = 20.0),
            ThreeForTwo(product = product)
        )

        // when
        cart.addItemQuantity(product = product, quantity = 5.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(20.0)
    }
}
