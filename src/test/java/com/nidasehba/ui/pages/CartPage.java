package com.nidasehba.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage {

    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public Locator items() {
        return page.getByTestId("inventory-item");
    }

    public CheckoutPage checkout() {
        page.getByTestId("checkout").click();
        return new CheckoutPage(page);
    }
}
