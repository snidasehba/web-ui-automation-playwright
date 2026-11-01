package com.nidasehba.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryPage {

    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
    }

    public Locator title() {
        return page.getByTestId("title");
    }

    public Locator items() {
        return page.getByTestId("inventory-item");
    }

    public Locator cartBadge() {
        return page.getByTestId("shopping-cart-badge");
    }

    public InventoryPage addToCart(String productName) {
        item(productName).locator("button").click();
        return this;
    }

    public InventoryPage removeFromCart(String productName) {
        return addToCart(productName); // the same button toggles between "Add to cart" and "Remove"
    }

    public Locator buttonFor(String productName) {
        return item(productName).locator("button");
    }

    public double priceOf(String productName) {
        return parsePrice(item(productName).getByTestId("inventory-item-price").innerText());
    }

    public InventoryPage sortBy(String option) {
        page.getByTestId("product-sort-container").selectOption(option);
        return this;
    }

    public List<Double> prices() {
        return page.getByTestId("inventory-item-price").allInnerTexts().stream()
                .map(InventoryPage::parsePrice).collect(Collectors.toList());
    }

    public List<String> names() {
        return page.getByTestId("inventory-item-name").allInnerTexts();
    }

    public CartPage openCart() {
        page.getByTestId("shopping-cart-link").click();
        return new CartPage(page);
    }

    private Locator item(String productName) {
        return items().filter(new Locator.FilterOptions().setHasText(productName));
    }

    static double parsePrice(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }
}
