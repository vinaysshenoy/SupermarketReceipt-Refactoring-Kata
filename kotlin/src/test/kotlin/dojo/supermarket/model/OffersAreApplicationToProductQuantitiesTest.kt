package dojo.supermarket.model

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.*
import strikt.assertions.*
import supermarket.ProductQuantities
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit
import supermarket.model.offers.BundleDiscountOffer
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

        val apples = Product("Apples", ProductUnit.Kilo)
        val oranges = Product("Oranges", ProductUnit.Kilo)

        return Stream.of(
            testCase(
                name = "10% off",
                offer = TenPercentDiscount(apples),
                productQuantities = mapOf(apples to 1.0)
            ),
            testCase(
                name = "3 for 2 applied to 3 items",
                offer = ThreeForTwo(apples),
                productQuantities = mapOf(apples to 3.0)
            ),
            testCase(
                name = "3 for 2 applied to 4 items",
                offer = ThreeForTwo(apples),
                productQuantities = mapOf(apples to 4.0)
            ),
            testCase(
                name = "x for amount applied with x = 2 and quantity 2",
                offer = XForAmount(
                    product = apples,
                    quantityForOffer = 2.0,
                    amount = 5.0
                ),
                productQuantities = mapOf(apples to 2.0)
            ),
            testCase(
                name = "x for amount applied with x = 5 and quantity 7",
                offer = XForAmount(
                    product = apples,
                    quantityForOffer = 5.0,
                    amount = 5.0
                ),
                productQuantities = mapOf(apples to 7.0)
            ),
            testCase(
                name = "bundle (apples: 1, oranges: 1.5) discount applied with apples = 1 and oranges = 1.5",
                offer = BundleDiscountOffer(
                    bundle = setOf(
                        ProductQuantity(apples, 1.0),
                        ProductQuantity(oranges, 1.5)
                    ),
                    discountPercent = 5.0
                ),
                productQuantities = mapOf(
                    apples to 1.0,
                    oranges to 1.5
                )
            ),
            testCase(
                name = "bundle (apples: 1, oranges: 1.5) discount applied with apples = 2 and oranges = 2.5",
                offer = BundleDiscountOffer(
                    bundle = setOf(
                        ProductQuantity(apples, 1.0),
                        ProductQuantity(oranges, 1.5)
                    ),
                    discountPercent = 5.0
                ),
                productQuantities = mapOf(
                    apples to 2.0,
                    oranges to 2.5
                )
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

        val apples = Product("Apples", ProductUnit.Kilo)
        val oranges = Product("Oranges", ProductUnit.Kilo)

        return Stream.of(
            testCase(
                name = "10% off",
                offer = TenPercentDiscount(apples),
                productQuantities = emptyMap()
            ),
            testCase(
                name = "3 for 2 applied to 2 items",
                offer = ThreeForTwo(apples),
                productQuantities = mapOf(apples to 2.0)
            ),
            testCase(
                name = "3 for 2 applied to 1 item",
                offer = ThreeForTwo(apples),
                productQuantities = mapOf(apples to 1.0)
            ),
            testCase(
                name = "x for amount applied with x = 2 and quantity 1",
                offer = XForAmount(apples, quantityForOffer = 2.0, amount = 5.0),
                productQuantities = mapOf(apples to 1.0)
            ),
            testCase(
                name = "x for amount applied with x = 5 and quantity 4",
                offer = XForAmount(apples, quantityForOffer = 5.0, amount = 5.0),
                productQuantities = mapOf(apples to 4.0)
            ),
            testCase(
                name = "bundle (apples: 1, oranges: 1.5) discount applied with apples = 1 and oranges = 1.49",
                offer = BundleDiscountOffer(
                    bundle = setOf(
                        ProductQuantity(apples, 1.0),
                        ProductQuantity(oranges, 1.5)
                    ),
                    discountPercent = 5.0
                ),
                productQuantities = mapOf(
                    apples to 1.0,
                    oranges to 1.49
                )
            ),
            testCase(
                name = "bundle (apples: 1, oranges: 1.5) discount applied with apples = 0.9 and oranges = 1.5",
                offer = BundleDiscountOffer(
                    bundle = setOf(
                        ProductQuantity(apples, 1.0),
                        ProductQuantity(oranges, 1.5)
                    ),
                    discountPercent = 5.0
                ),
                productQuantities = mapOf(
                    apples to 0.9,
                    oranges to 1.5
                )
            )
        )
    }
}
