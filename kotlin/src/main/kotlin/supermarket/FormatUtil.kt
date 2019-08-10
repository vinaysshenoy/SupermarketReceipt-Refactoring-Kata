package supermarket

import supermarket.model.ProductUnit
import java.util.*

fun formatQuantity(unit: ProductUnit, quantity: Double): String {
    return when (unit) {
        ProductUnit.Each -> String.format(Locale.UK, "%x", quantity.toInt())
        ProductUnit.Kilo -> String.format(Locale.UK, "%.3f", quantity)
    }
}
