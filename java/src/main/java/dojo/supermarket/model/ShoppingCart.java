package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    Map<Product, Double> productQuantities = new HashMap<>();


    List<ProductQuantity> getItems() {
        return new ArrayList<>(items);
    }

    void addItem(Product product) {
        this.addItemQuantity(product, 1.0);
    }

    Map<Product, Double> productQuantities() {
        return productQuantities;
    }


    public void addItemQuantity(Product product, double quantity) {
        items.add(new ProductQuantity(product, quantity));
        if (productQuantities.containsKey(product)) {
            productQuantities.put(product, productQuantities.get(product) + quantity);
        } else {
            productQuantities.put(product, quantity);
        }
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog) {
        for (Product p : productQuantities().keySet()) {
            handleProduct(receipt, offers, catalog, p);
        }
    }

    private void handleProduct(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog, Product p) {
        if (offers.containsKey(p)) {
            Bang(receipt, offers, catalog, p, productQuantities.get(p));
        }
    }

    private void Bang(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog, Product p, double quantity) {
        Offer offer = offers.get(p);
        double unitPrice = catalog.getUnitPrice(p);
        int quantityAsInt = (int) quantity;
        Discount discount = getDiscount(p, quantity, offer, unitPrice, quantityAsInt);
        if (discount != null)
            receipt.addDiscount(discount);
    }

    private Discount getDiscount(Product p, double quantity, Offer offer, double unitPrice, int quantityAsInt) {
        Discount discount = null;
        int offerCount = 1;
        if (offer.offerType == SpecialOfferType.ThreeForTwo) {
            offerCount = 3;

        } else if (offer.offerType == SpecialOfferType.TwoForAmount) {
            offerCount = 2;
            if (quantityAsInt >= 2) {
                double total = offer.argument * quantityAsInt / offerCount + quantityAsInt % 2 * unitPrice;
                double discountN = unitPrice * quantity - total;
                discount = new Discount(p, "2 for " + offer.argument, discountN);
            }

        }else if (offer.offerType == SpecialOfferType.FiveForAmount) {
            offerCount = 5;
        }
        int numberOfOffers = quantityAsInt / offerCount;
        if (offer.offerType == SpecialOfferType.ThreeForTwo && quantityAsInt > 2) {
            double discountAmount = quantity * unitPrice - ((numberOfOffers * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
            discount = new Discount(p, "3 for 2", discountAmount);
        }
        if (offer.offerType == SpecialOfferType.TenPercentDiscount) {
            discount = new Discount(p, offer.argument + "% off", quantity * unitPrice * offer.argument / 100.0);
        }
        if (offer.offerType == SpecialOfferType.FiveForAmount && quantityAsInt >= 5) {
            double discountTotal = unitPrice * quantity - (offer.argument * numberOfOffers + quantityAsInt % 5 * unitPrice);
            discount = new Discount(p, offerCount + " for " + offer.argument, discountTotal);
        }
        return discount;
    }
}
