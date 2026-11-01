package com.nidasehba.ui.tests;

import com.nidasehba.ui.base.BaseUiTest;
import com.nidasehba.ui.config.Config;
import com.nidasehba.ui.pages.CartPage;
import com.nidasehba.ui.pages.InventoryPage;
import com.nidasehba.ui.pages.LoginPage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

public class CartAndSortTest extends BaseUiTest {

    private InventoryPage loggedIn() {
        return new LoginPage(page()).open().loginAs("standard_user", Config.password());
    }

    @Test(groups = {"smoke", "regression"})
    public void addingProductsUpdatesCartBadgeAndCart() {
        InventoryPage inventory = loggedIn()
                .addToCart("Sauce Labs Backpack")
                .addToCart("Sauce Labs Bike Light");

        assertThat(inventory.cartBadge()).hasText("2");
        assertThat(inventory.buttonFor("Sauce Labs Backpack")).hasText("Remove");

        CartPage cart = inventory.openCart();
        assertThat(cart.items()).hasCount(2);
    }

    @Test(groups = "regression")
    public void removingAProductUpdatesTheBadge() {
        InventoryPage inventory = loggedIn()
                .addToCart("Sauce Labs Backpack")
                .addToCart("Sauce Labs Onesie")
                .removeFromCart("Sauce Labs Backpack");

        assertThat(inventory.cartBadge()).hasText("1");
        assertThat(inventory.buttonFor("Sauce Labs Backpack")).hasText("Add to cart");
    }

    @Test(groups = "regression")
    public void sortsByPriceLowToHigh() {
        List<Double> prices = loggedIn().sortBy("lohi").prices();

        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Comparator.naturalOrder());
        assertEquals(prices, expected, "prices are in ascending order");
    }

    @Test(groups = "regression")
    public void sortsByNameZToA() {
        List<String> names = loggedIn().sortBy("za").names();

        List<String> expected = new ArrayList<>(names);
        expected.sort(Comparator.reverseOrder());
        assertEquals(names, expected, "names are in reverse alphabetical order");
    }
}
