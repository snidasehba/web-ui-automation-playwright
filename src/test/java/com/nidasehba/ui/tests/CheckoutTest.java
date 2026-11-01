package com.nidasehba.ui.tests;

import com.nidasehba.ui.base.BaseUiTest;
import com.nidasehba.ui.config.Config;
import com.nidasehba.ui.pages.CheckoutPage;
import com.nidasehba.ui.pages.InventoryPage;
import com.nidasehba.ui.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

public class CheckoutTest extends BaseUiTest {

    @Test(groups = {"smoke", "regression"}, description = "End-to-end order with a price cross-check")
    public void completesOrderAndTotalsAddUp() {
        InventoryPage inventory = new LoginPage(page()).open().loginAs("standard_user", Config.password());
        double backpack = inventory.priceOf("Sauce Labs Backpack");
        double jacket = inventory.priceOf("Sauce Labs Fleece Jacket");

        CheckoutPage checkout = inventory
                .addToCart("Sauce Labs Backpack")
                .addToCart("Sauce Labs Fleece Jacket")
                .openCart()
                .checkout()
                .enterDetails("Nida", "Tester", "560001");

        assertEquals(checkout.itemTotal(), backpack + jacket, 0.001, "item total = sum of product prices");
        assertEquals(checkout.total(), checkout.itemTotal() + checkout.tax(), 0.001, "total = item total + tax");

        checkout.finish();
        assertThat(checkout.confirmationHeader()).hasText("Thank you for your order!");
    }

    @DataProvider(name = "missingDetails")
    public Object[][] missingDetails() {
        return new Object[][]{
                {"", "Tester", "560001", "First Name is required"},
                {"Nida", "", "560001", "Last Name is required"},
                {"Nida", "Tester", "", "Postal Code is required"},
        };
    }

    @Test(dataProvider = "missingDetails", groups = "regression")
    public void checkoutValidatesCustomerDetails(String first, String last, String zip, String message) {
        CheckoutPage checkout = new LoginPage(page()).open().loginAs("standard_user", Config.password())
                .addToCart("Sauce Labs Bolt T-Shirt")
                .openCart()
                .checkout()
                .enterDetails(first, last, zip);

        assertThat(checkout.error()).containsText(message);
    }
}
