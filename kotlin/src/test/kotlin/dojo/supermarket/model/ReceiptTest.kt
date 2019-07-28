package dojo.supermarket.model

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEmpty
import supermarket.model.*

class ReceiptTest {

    @Test
    fun `receipt for cart with 0 items`() {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)

        catalog.apply {
            addProduct(product = product1, price = 10.0)
            addProduct(product = product2, price = 0.5)
        }

        // when
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        expect {
            that(receipt.getItems()).isEmpty()
            that(receipt.getDiscounts()).isEmpty()
        }
    }

    @Test
    fun `receipt for cart with items and discounts`() {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product that has a really, really, really long name", ProductUnit.Each)
        val product3 = Product("product 3", ProductUnit.Kilo)
        val product4 = Product("product 4", ProductUnit.Each)
        val product5 = Product("product 5", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = product1, price = 5.0)
            addProduct(product = product2, price = 10.0)
            addProduct(product = product3, price = 15.0)
            addProduct(product = product4, price = 20.0)
            addProduct(product = product5, price = 25.0)
        }

        teller.apply {
            addSpecialOffer(offerType = SpecialOfferType.ThreeForTwo, product = product3, argument = -1.0)
            addSpecialOffer(offerType = SpecialOfferType.TwoForAmount, product = product1, argument = 18.5)
            addSpecialOffer(offerType = SpecialOfferType.TenPercentDiscount, product = product5, argument = 10.0)
            addSpecialOffer(offerType = SpecialOfferType.FiveForAmount, product = product4, argument = 95.0)
        }

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product4, quantity = 7.5)
            addItemQuantity(product = product2, quantity = 3.0)
            addItemQuantity(product = product1, quantity = 2.0)
            addItemQuantity(product = product3, quantity = 2.0)
            addItemQuantity(product = product5, quantity = 3.5)
            addItem(product = product3)
        }

        // when
        val receipt = teller.checksOutArticlesFrom(cart)

        // then
        val expectedReceiptItems = listOf(
            ReceiptItem(product = product1, quantity = 2.0, price = 5.0, totalPrice = 10.0),
            ReceiptItem(product = product4, quantity = 7.5, price = 20.0, totalPrice = 150.0),
            ReceiptItem(product = product2, quantity = 3.0, price = 10.0, totalPrice = 30.0),
            ReceiptItem(product = product1, quantity = 2.0, price = 5.0, totalPrice = 10.0),
            ReceiptItem(product = product3, quantity = 2.0, price = 15.0, totalPrice = 30.0),
            ReceiptItem(product = product5, quantity = 3.5, price = 25.0, totalPrice = 87.5),
            ReceiptItem(product = product3, quantity = 1.0, price = 15.0, totalPrice = 15.0)
        )
        val expectedDiscounts = listOf(
            Discount(product = product1, description = "2 for 18.5", discountAmount = -17.0),
            Discount(product = product4, description = "5 for 95.0", discountAmount = 15.0),
            Discount(product = product3, description = "3 for 2", discountAmount = 15.0),
            Discount(product = product5, description = "10.0% off", discountAmount = 8.75)
        )

        expect {
            that(receipt.getItems())
                .containsExactlyInAnyOrder(expectedReceiptItems)
            that(receipt.getDiscounts())
                .containsExactlyInAnyOrder(expectedDiscounts)
        }
    }

    @Test
    fun `receipt for cart with items and offers not applicable to items`() {
        // given
        val catalog = FakeCatalog()
        val cart = ShoppingCart()
        val teller = Teller(catalog)

        val product1 = Product("product 1", ProductUnit.Kilo)
        val product2 = Product("product 2", ProductUnit.Each)
        val product3 = Product("product 3", ProductUnit.Kilo)
        val product4 = Product("product 4", ProductUnit.Each)
        val product5 = Product("product 5", ProductUnit.Kilo)

        catalog.apply {
            addProduct(product = product1, price = 5.0)
            addProduct(product = product2, price = 10.0)
            addProduct(product = product3, price = 15.0)
            addProduct(product = product4, price = 20.0)
            addProduct(product = product5, price = 25.0)
        }

        teller.apply {
            addSpecialOffer(offerType = SpecialOfferType.ThreeForTwo, product = product3, argument = -1.0)
            addSpecialOffer(offerType = SpecialOfferType.TwoForAmount, product = product1, argument = 18.5)
            addSpecialOffer(offerType = SpecialOfferType.TenPercentDiscount, product = product5, argument = 10.0)
            addSpecialOffer(offerType = SpecialOfferType.FiveForAmount, product = product4, argument = 95.0)
        }

        // when
        cart.apply {
            addItemQuantity(product = product1, quantity = 1.0)
            addItemQuantity(product = product4, quantity = 4.5)
            addItemQuantity(product = product2, quantity = 3.0)
            addItemQuantity(product = product3, quantity = 2.0)
            addItemQuantity(product = product5, quantity = 3.5)
        }

        // when
        val receipt = teller.checksOutArticlesFrom(cart)


        // then
        val expectedProductItems = listOf(
            ReceiptItem(product = product1, quantity = 1.0, price = 5.0, totalPrice = 5.0),
            ReceiptItem(product = product4, quantity = 4.5, price = 20.0, totalPrice = 90.0),
            ReceiptItem(product = product2, quantity = 3.0, price = 10.0, totalPrice = 30.0),
            ReceiptItem(product = product3, quantity = 2.0, price = 15.0, totalPrice = 30.0),
            ReceiptItem(product = product5, quantity = 3.5, price = 25.0, totalPrice = 87.5)
        )

        val expectedDiscounts = listOf(
            Discount(product=product5, description="10.0% off", discountAmount=8.75)
        )

        expect {
            that(receipt.getItems())
                .containsExactlyInAnyOrder(expectedProductItems)
            that(receipt.getDiscounts())
                .containsExactlyInAnyOrder(expectedDiscounts)
        }
    }
}
