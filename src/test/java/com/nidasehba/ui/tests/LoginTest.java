package com.nidasehba.ui.tests;

import com.nidasehba.ui.base.BaseUiTest;
import com.nidasehba.ui.config.Config;
import com.nidasehba.ui.pages.InventoryPage;
import com.nidasehba.ui.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest extends BaseUiTest {

    @Test(groups = {"smoke", "regression"})
    public void standardUserCanLogIn() {
        InventoryPage inventory = new LoginPage(page()).open().loginAs("standard_user", Config.password());

        assertThat(page()).hasURL(java.util.regex.Pattern.compile(".*/inventory\\.html"));
        assertThat(inventory.title()).hasText("Products");
        assertThat(inventory.items()).hasCount(6);
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][]{
                {"locked_out_user", Config.password(), "Sorry, this user has been locked out."},
                {"standard_user", "wrong_password", "Username and password do not match"},
                {"", Config.password(), "Username is required"},
                {"standard_user", "", "Password is required"},
        };
    }

    @Test(dataProvider = "invalidLogins", groups = "regression")
    public void invalidLoginShowsError(String user, String password, String expectedMessage) {
        LoginPage login = new LoginPage(page()).open().submit(user, password);

        assertThat(login.error()).containsText(expectedMessage);
        assertThat(page()).hasURL(Config.baseUrl() + "/");
    }

    @Test(groups = "regression", description = "Protected pages redirect to login when there is no session")
    public void inventoryRequiresLogin() {
        page().navigate("/inventory.html");

        assertThat(new LoginPage(page()).error()).containsText("You can only access '/inventory.html' when you are logged in");
    }
}
