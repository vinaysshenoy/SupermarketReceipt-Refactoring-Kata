package dojo.supermarket.model

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.*
import strikt.assertions.*
import supermarket.ProductQuantities
import supermarket.model.Product
import supermarket.model.ProductUnit
import supermarket.model.offers.Offer
import supermarket.model.offers.TenPercentDiscount
import supermarket.model.offers.ThreeForTwo
import supermarket.model.offers.XForAmount
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OffersAreApplicationToProductQuantitiesTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("params for offers applicable to products")
    fun `when an offer can be applied to product quantities, it must return true`(
        name: String,
        offer: Offer,
        productQuantities: ProductQuantities
    ) {
        // when
        val isOfferApplicableToProducts = offer.isOfferApplicable(productQuantities)

        // then
        expectThat(isOfferApplicableToProducts).isTrue()
    }

    @Suppress("Unused")
    private fun `params for offers applicable to products`(): Stream<Arguments> {
        fun testCase(
            name: String,
            offer: Offer,
            productQuantities: ProductQuantities
        ): Arguments {
            return Arguments.of(name, offer, productQuantities)
        }

        val product = Product("product", ProductUnit.Kilo)

        return Stream.of(
            testCase(
                name = "10% off",
                offer = TenPercentDiscount(product),
                productQuantities = mapOf(product to 1.0)
            ),
            testCase(
                name = "3 for 2 applied to 3 items",
                offer = ThreeForTwo(product),
                productQuantities = mapOf(product to 3.0)
            ),
            testCase(
                name = "3 for 2 applied to 4 items",
                offer = ThreeForTwo(product),
                productQuantities = mapOf(product to 4.0)
            ),
            testCase(
                name = "x for amount applied with x = 2 and quantity 2",
                offer = XForAmount(product, quantityForOffer = 2.0, amount = 5.0),
                productQuantities = mapOf(product to 2.0)
            ),
            testCase(
                name = "x for amount applied with x = 5 and quantity 7",
                offer = XForAmount(product, quantityForOffer = 5.0, amount = 5.0),
                productQuantities = mapOf(product to 7.0)
            )
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params for offers not applicable to products")
    fun `when an offer cannot be applied to product quantities, it must return false`(
        name: String,
        offer: Offer,
        productQuantities: ProductQuantities
    ) {
        // when
        val isOfferApplicableToProducts = offer.isOfferApplicable(productQuantities)

        // then
        expectThat(isOfferApplicableToProducts).isFalse()
    }

    @Suppress("Unused")
    private fun `params for offers not applicable to products`(): Stream<Arguments> {
        fun testCase(
            name: String,
            offer: Offer,
            productQuantities: ProductQuantities
        ): Arguments {
            return Arguments.of(name, offer, productQuantities)
        }

        val product = Product("product", ProductUnit.Kilo)

        return Stream.of(
            testCase(
                name = "10% off",
                offer = TenPercentDiscount(product),
                productQuantities = emptyMap()
            ),
            testCase(
                name = "3 for 2 applied to 2 items",
                offer = ThreeForTwo(product),
                productQuantities = mapOf(product to 2.0)
            ),
            testCase(
                name = "3 for 2 applied to 1 item",
                offer = ThreeForTwo(product),
                productQuantities = mapOf(product to 1.0)
            ),
            testCase(
                name = "x for amount applied with x = 2 and quantity 1",
                offer = XForAmount(product, quantityForOffer = 2.0, amount = 5.0),
                productQuantities = mapOf(product to 1.0)
            ),
            testCase(
                name = "x for amount applied with x = 5 and quantity 4",
                offer = XForAmount(product, quantityForOffer = 5.0, amount = 5.0),
                productQuantities = mapOf(product to 4.0)
            )
        )
    }
}
