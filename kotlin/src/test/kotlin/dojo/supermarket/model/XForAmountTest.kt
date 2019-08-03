package dojo.supermarket.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.*
import strikt.assertions.*
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.ProductUnit
import supermarket.model.offers.XForAmount

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XForAmountTest {

    @Test
    fun `the applicable product must be returned`() {
        // given
        val product = Product(name = "product", unit = ProductUnit.Each)
        val offer = XForAmount(product = product, quantityForOffer = 2.0, amount = 0.0)

        // when
        val applicableProducts = offer.applicableProducts()

        // then
        expectThat(applicableProducts).containsExactly(product)
    }

    data class ParamsForApplyingDiscountsTest(
        val product: Product,
        val priceOfProduct: Double,
        val quantityForOffer: Double,
        val amountAfterDiscount: Double,
        val actualQuantityOfProduct: Double,
        val expectedTotalDiscount: Double
    )

    @DisplayName("discounts must be applied")
    @ParameterizedTest(name = "#{index}")
    @MethodSource("params for applying discounts")
    fun `discounts must be applied`(testCase: ParamsForApplyingDiscountsTest) {
        val (product: Product,
            priceOfProduct: Double,
            quantityForOffer: Double,
            amountAfterDiscount: Double,
            actualQuantityOfProduct: Double,
            expectedDiscount: Double) = testCase

        // given
        val catalog = FakeCatalog()
        catalog.addProduct(product = product, price = priceOfProduct)

        val productQuantities = mapOf(
            product to actualQuantityOfProduct
        )
        val offer = XForAmount(
            product = product,
            quantityForOffer = quantityForOffer,
            amount = amountAfterDiscount
        )

        // when
        val discount: Discount = offer.discount(
            productQuantities = productQuantities,
            catalog = catalog
        )!!

        // then
        expect {
            val productsInDiscount = discount
                .products
                .map { it.product }
                .toSet()

            that(productsInDiscount).isEqualTo(setOf(product))
            that(discount.discountAmount).isEqualTo(expectedDiscount, tolerance = 0.001)
        }
    }

    @Suppress("Unused")
    private fun `params for applying discounts`(): List<ParamsForApplyingDiscountsTest> {

        return listOf(
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Kilo),
                priceOfProduct = 10.0,
                quantityForOffer = 3.0,
                amountAfterDiscount = 25.0,
                actualQuantityOfProduct = 4.0,
                expectedTotalDiscount = 5.0
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Kilo),
                priceOfProduct = 10.0,
                quantityForOffer = 3.0,
                amountAfterDiscount = 25.0,
                actualQuantityOfProduct = 5.0,
                expectedTotalDiscount = 5.0
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Kilo),
                priceOfProduct = 10.0,
                quantityForOffer = 3.0,
                amountAfterDiscount = 25.0,
                actualQuantityOfProduct = 6.0,
                expectedTotalDiscount = 10.0
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Kilo),
                priceOfProduct = 10.0,
                quantityForOffer = 3.0,
                amountAfterDiscount = 25.0,
                actualQuantityOfProduct = 7.0,
                expectedTotalDiscount = 10.0
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Each),
                priceOfProduct = 55.75,
                quantityForOffer = 2.0,
                amountAfterDiscount = 99.99,
                actualQuantityOfProduct = 2.0,
                expectedTotalDiscount = 11.51
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Each),
                priceOfProduct = 55.75,
                quantityForOffer = 2.0,
                amountAfterDiscount = 99.99,
                actualQuantityOfProduct = 3.0,
                expectedTotalDiscount = 11.51
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Each),
                priceOfProduct = 55.75,
                quantityForOffer = 2.0,
                amountAfterDiscount = 99.99,
                actualQuantityOfProduct = 4.0,
                expectedTotalDiscount = 23.02
            ),
            ParamsForApplyingDiscountsTest(
                product = Product(name = "product", unit = ProductUnit.Each),
                priceOfProduct = 55.75,
                quantityForOffer = 2.0,
                amountAfterDiscount = 99.99,
                actualQuantityOfProduct = 5.0,
                expectedTotalDiscount = 23.02
            )
        )
    }
}
