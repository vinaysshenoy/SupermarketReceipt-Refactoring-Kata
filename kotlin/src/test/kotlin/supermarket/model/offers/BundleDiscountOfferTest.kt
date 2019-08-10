package supermarket.model.offers

import dojo.supermarket.model.FakeCatalog
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.*
import strikt.assertions.*
import supermarket.ProductQuantities
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.ProductUnit.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BundleDiscountOfferTest {

    @Test
    fun `the applicable products must be returned`() {
        // given
        val product1 = Product(name = "Apple", unit = Kilo)
        val product2 = Product(name = "Fruit and Vegetable Peeler", unit = Each)
        val bundle = setOf(
            ProductQuantity(product1, 1.0),
            ProductQuantity(product2, 1.0)
        )
        val offer = BundleDiscountOffer(bundle = bundle, discountPercent = 0.0)

        // when
        val applicableProducts = offer.applicableProducts()

        // then
        expectThat(applicableProducts)
            .containsExactlyInAnyOrder(product1, product2)
    }

    data class ParamsForApplyingDiscount(
        val bundle: Set<ProductQuantity>,
        val prices: Map<Product, Double>,
        val discountPercent: Double,
        val quantitiesOfProduct: Set<ProductQuantity>,
        val expectedDiscountProducts: Set<ProductQuantity>,
        val expectedDiscount: Double
    )

    @ParameterizedTest(name = "#{index}")
    @MethodSource("params for applying discounts")
    fun `discounts must be applied`(testCase: ParamsForApplyingDiscount) {

        val (bundle: Set<ProductQuantity>,
            prices: Map<Product, Double>,
            discountPercent: Double,
            quantitiesOfProduct: Set<ProductQuantity>,
            expectedDiscountProducts: Set<ProductQuantity>,
            expectedDiscount: Double
        ) = testCase

        // given
        val catalog = FakeCatalog()
        prices.forEach { (product, price) -> catalog.addProduct(product, price) }

        val productQuantities: ProductQuantities = quantitiesOfProduct
            .associateBy({ (product, _) -> product }, { (_, quantity) -> quantity })

        val offer = BundleDiscountOffer(bundle, discountPercent)

        // when
        val discount = offer.discount(productQuantities, catalog)

        // then
        expect {
            that(discount.products).isEqualTo(expectedDiscountProducts)
            that(discount.discountAmount).isEqualTo(expectedDiscount, tolerance = 0.001)
        }
    }

    @Suppress("Unused")
    private fun `params for applying discounts`(): List<ParamsForApplyingDiscount> {
        val apples = Product("Apples", Kilo)
        val oranges = Product("Oranges", Kilo)
        val peelers = Product("Peeler", Each)
        val toothpaste = Product("Toothpaste", Each)
        val toothbrush = Product("Toothbrush", Each)
        val mouthwash = Product("Mouthwash", Each)

        return listOf(
            ParamsForApplyingDiscount(
                bundle = setOf(
                    ProductQuantity(apples, 1.0),
                    ProductQuantity(oranges, 1.0)
                ),
                prices = mapOf(
                    apples to 5.0,
                    oranges to 3.0
                ),
                discountPercent = 8.0,
                quantitiesOfProduct = setOf(
                    ProductQuantity(apples, 1.5),
                    ProductQuantity(oranges, 1.0)
                ),
                expectedDiscountProducts = setOf(
                    ProductQuantity(apples, 1.0),
                    ProductQuantity(oranges, 1.0)
                ),
                expectedDiscount = 0.64
            ),
            ParamsForApplyingDiscount(
                bundle = setOf(
                    ProductQuantity(apples, 1.0),
                    ProductQuantity(peelers, 1.0)
                ),
                prices = mapOf(
                    apples to 5.0,
                    peelers to 1.25
                ),
                discountPercent = 13.5,
                quantitiesOfProduct = setOf(
                    ProductQuantity(apples, 1.0),
                    ProductQuantity(peelers, 1.0)
                ),
                expectedDiscountProducts = setOf(
                    ProductQuantity(apples, 1.0),
                    ProductQuantity(peelers, 1.0)
                ),
                expectedDiscount = 0.843
            ),
            ParamsForApplyingDiscount(
                bundle = setOf(
                    ProductQuantity(toothbrush, 1.0),
                    ProductQuantity(toothpaste, 1.0),
                    ProductQuantity(mouthwash, 1.0)
                ),
                prices = mapOf(
                    toothbrush to 2.5,
                    toothpaste to 3.0,
                    mouthwash to 3.5
                ),
                discountPercent = 15.0,
                quantitiesOfProduct = setOf(
                    ProductQuantity(toothbrush, 2.0),
                    ProductQuantity(toothpaste, 1.0),
                    ProductQuantity(mouthwash, 1.0)
                ),
                expectedDiscountProducts = setOf(
                    ProductQuantity(toothbrush, 1.0),
                    ProductQuantity(toothpaste, 1.0),
                    ProductQuantity(mouthwash, 1.0)
                ),
                expectedDiscount = 1.35
            ),
            ParamsForApplyingDiscount(
                bundle = setOf(
                    ProductQuantity(apples, 1.5),
                    ProductQuantity(oranges, 1.0)
                ),
                prices = mapOf(
                    apples to 5.0,
                    oranges to 3.0
                ),
                discountPercent = 8.0,
                quantitiesOfProduct = setOf(
                    ProductQuantity(apples, 4.0),
                    ProductQuantity(oranges, 4.0)
                ),
                expectedDiscountProducts = setOf(
                    ProductQuantity(apples, 3.0),
                    ProductQuantity(oranges, 2.0)
                ),
                expectedDiscount = 1.68
            )
        )
    }
}
