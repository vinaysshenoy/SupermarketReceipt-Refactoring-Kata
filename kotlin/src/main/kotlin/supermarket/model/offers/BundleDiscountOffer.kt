package supermarket.model.offers

import supermarket.ProductQuantities
import supermarket.formatQuantity
import supermarket.model.Discount
import supermarket.model.Product
import supermarket.model.ProductQuantity
import supermarket.model.SupermarketCatalog

data class BundleDiscountOffer(
    val bundle: Set<ProductQuantity>,
    val discountPercent: Double
) : Offer {

    init {
        if (bundle.size <= 1) throw IllegalArgumentException("Bundle must have at least 2 products!")
    }

    override fun discount(allProducts: ProductQuantities, catalog: SupermarketCatalog): Discount {
        val applicableProductsInDiscount: Set<Product> = applicableProducts()

        val productQuantitiesInOffer: Set<ProductQuantity> = allProducts
            .filter { (product, _) -> product in applicableProductsInDiscount }
            .filter { (product, totalQuantity) -> totalQuantity >= minimumQuantityOfProductInBundle(product) }
            .map { (product, totalQuantity) -> ProductQuantity(product, totalQuantity) }
            .toSet()

        val occurrencesOfBundle: Int = bundle
            .map { productQuantity -> occurrencesOfProductWithQuantity(productQuantity, productQuantitiesInOffer) }
            .toList()
            // The only case where !! can be thrown is if the list
            // returns no elements, i.e, the bundle is an empty set.
            // We already check for this during initialization.
            .min()!!

        val combinedUnitPriceOfBundle: Double = bundle
            .map { (product, quantity) -> quantity * catalog.getUnitPrice(product) }
            .sum()

        val totalPriceOfBundleProductsBeforeDiscount = occurrencesOfBundle * combinedUnitPriceOfBundle
        val discountAmount = totalPriceOfBundleProductsBeforeDiscount * (discountPercent / 100.0)

        val discountProducts = productQuantitiesInOffer
            .map { productQuantity -> productQuantityIncludedInBundle(productQuantity, occurrencesOfBundle) }
            .toSet()

        return Discount(discountProducts, generateDiscountDescription(), discountAmount)
    }

    private fun productQuantityIncludedInBundle(
        productQuantity: ProductQuantity,
        occurrencesOfBundle: Int
    ): ProductQuantity {
        return ProductQuantity(
            product = productQuantity.product,
            quantity = bundle.find { it.product == productQuantity.product }!!.quantity * occurrencesOfBundle
        )
    }

    private fun occurrencesOfProductWithQuantity(
        bundleProductToFind: ProductQuantity,
        products: Set<ProductQuantity>
    ): Int {
        val foundProduct = products.find { it.product == bundleProductToFind.product }

        return foundProduct?.quantity?.div(bundleProductToFind.quantity)?.toInt() ?: 0
    }

    private fun minimumQuantityOfProductInBundle(product: Product): Double {
        return bundle.find { it.product == product }!!.quantity
    }

    private fun generateDiscountDescription(): String {
        return "%.02f%% off(%s)".format(discountPercent, generateBundlePresentation())
    }

    private fun generateBundlePresentation() = bundle.joinToString(" + ", transform = this::presentProductQuantity)

    private fun presentProductQuantity(productQuantity: ProductQuantity): String {
        val product = productQuantity.product
        val quantity = productQuantity.quantity

        return "${product.name} ${formatQuantity(product.unit, quantity)}"
    }

    override fun applicableProducts(): Set<Product> {
        return bundle
            .map { it.product }
            .toSet()
    }

    override fun isOfferApplicable(productQuantities: ProductQuantities): Boolean {
        return bundle
            .map { (bundleProduct, minimumQuantity) ->
                productQuantities.getOrDefault(
                    bundleProduct,
                    0.0
                ) >= minimumQuantity
            }
            .reduce { isBundleApplicable, isProductQuantityGreaterThanMinimumBundleQuantity ->
                isBundleApplicable && isProductQuantityGreaterThanMinimumBundleQuantity
            }
    }
}
