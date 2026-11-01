package com.nidasehba.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Covers the three checkout steps: customer details, order overview and confirmation. */
public class CheckoutPage {

    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public CheckoutPage enterDetails(String firstName, String lastName, String postalCode) {
        page.getByTestId("firstName").fill(firstName);
        page.getByTestId("lastName").fill(lastName);
        page.getByTestId("postalCode").fill(postalCode);
        page.getByTestId("continue").click();
        return this;
    }

    public Locator error() {
        return page.getByTestId("error");
    }

    public double itemTotal() {
        return InventoryPage.parsePrice(page.getByTestId("subtotal-label").innerText());
    }

    public double tax() {
        return InventoryPage.parsePrice(page.getByTestId("tax-label").innerText());
    }

    public double total() {
        return InventoryPage.parsePrice(page.getByTestId("total-label").innerText());
    }

    public CheckoutPage finish() {
        page.getByTestId("finish").click();
        return this;
    }

    public Locator confirmationHeader() {
        return page.getByTestId("complete-header");
    }
}
