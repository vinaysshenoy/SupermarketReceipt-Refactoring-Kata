package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import supermarket.model.*
import supermarket.model.SpecialOfferType.*

@DisplayName("Ten percent discount offer test")
class TenPercentDiscountOfferTest {

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
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product, argument = 10.0)

        // when
        cart.addItemQuantity(product = product, quantity = 1.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(4.5)
    }

    @DisplayName("offer applied to only one item with multiple quantity")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 2`(productUnit: ProductUnit) {
        // given
        val product = Product("product", productUnit)

        catalog.addProduct(product = product, price = 5.5)
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product, argument = 10.0)

        // when
        cart.addItemQuantity(product = product, quantity = 12.0)
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(59.4)
    }

    @DisplayName("offer on first item applied to cart with two items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 3`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product 1", productUnit)
        val product2 = Product("product 2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 15.0)
        }
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product1, argument = 10.0)

        // when
        cart.apply {
            addItemQuantity(product = product2, quantity = 5.0)
            addItemQuantity(product = product1, quantity = 5.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(120.0)
    }

    @DisplayName("offer on second item applied to cart with two items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 4`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product 1", productUnit)
        val product2 = Product("product 2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 15.0)
        }
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product2, argument = 10.0)

        // when
        cart.apply {
            addItemQuantity(product = product2, quantity = 5.0)
            addItemQuantity(product = product1, quantity = 5.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(117.5)
    }

    @DisplayName("offer on both items applied to cart with two items")
    @ParameterizedTest(name = "Product Unit: {0}")
    @EnumSource(ProductUnit::class)
    fun `test 5`(productUnit: ProductUnit) {
        // given
        val product1 = Product("product 1", productUnit)
        val product2 = Product("product 2", productUnit)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 15.0)
        }
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product1, argument = 10.0)
        teller.addSpecialOffer(offerType = TenPercentDiscount, product = product2, argument = 10.0)

        // when
        cart.apply {
            addItemQuantity(product = product2, quantity = 5.0)
            addItemQuantity(product = product1, quantity = 5.0)
        }
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expectThat(receipt.totalPrice).isEqualTo(112.5)
    }
}
