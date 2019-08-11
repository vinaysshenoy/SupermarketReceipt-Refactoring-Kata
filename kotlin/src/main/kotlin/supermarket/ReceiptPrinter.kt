package supermarket

import supermarket.model.Receipt
import supermarket.receiptgenerators.ReceiptGenerator
import java.util.*

class ReceiptPrinter {

    private var generators: Map<String, ReceiptGenerator> = emptyMap()

    fun register(format: String, generator: ReceiptGenerator) {
        generators = generators.plus(normalize(format) to generator)
    }

    fun printReceipt(format: String, receipt: Receipt, options: Map<String, Any> = emptyMap()): String {
        val normalizedFormat = normalize(format)

        return generators
            .getOrElse(normalizedFormat) { throw UnknownFormatException(normalizedFormat) }
            .generate(receipt, options)
    }

    private fun normalize(format: String): String {
        return format.toUpperCase(Locale.US)
    }

    data class UnknownFormatException(
        val format: String
    ) : IllegalArgumentException("Could not find generator for format: [$format]")
}
