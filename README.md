# Web UI Automation with Playwright (Java)

End-to-end UI tests for an online store, written in **Java 17** with **Playwright** and **TestNG**,
using the **Page Object Model**, running in parallel and in **GitHub Actions** (a GitLab CI file is included too) on Chromium and Firefox.

The application under test is [Sauce Demo](https://www.saucedemo.com), a public practice site
provided by Sauce Labs for test automation.

## What the tests cover

| Area | Examples |
|---|---|
| Login | Valid login, locked-out user, wrong password, empty fields (data-driven), protected page without a session |
| Cart | Add and remove products, cart badge count, cart contents |
| Sorting | Price low → high and name Z → A, verified against a sorted copy of what the page shows |
| Checkout | Full order flow; item total equals the sum of product prices, and total equals item total + tax |
| Form validation | Missing first name, last name or postal code (data-driven) |

## Structure

```
src/test/java/com/nidasehba/ui/
  config/Config.java        run settings via -D properties (browser, headless, baseUrl)
  base/BaseUiTest.java      browser per thread, fresh context per test, screenshot + trace on failure
  pages/                    page objects: Login, Inventory, Cart, Checkout
  tests/                    test classes (groups: smoke, regression)
src/test/resources/testng.xml   parallel suite
.github/workflows/ci.yml    Chromium + Firefox runs (also .gitlab-ci.yml)
```

## Run it

```bash
mvn test                                   # headless Chromium (downloads browsers on first run)
mvn test -Dgroups=smoke                    # smoke subset
mvn test -Dbrowser=firefox                 # another engine
mvn test -Dheadless=false                  # watch it run
mvn test -Dchannel=chrome                  # use the installed Google Chrome instead
```

When a test fails, `target/screenshots/` holds a full-page screenshot and `target/traces/` a
Playwright trace you can open with `npx playwright show-trace <file>.zip` or at trace.playwright.dev.

## Design choices

- **Locators on `data-test` attributes** (`setTestIdAttribute("data-test")`), not CSS classes, so styling changes don't break tests.
- **Web-first assertions** (`assertThat(locator).hasText(...)`) wait for the UI instead of fixed sleeps.
- **Isolation**: a new browser context per test means no shared cookies or cart, which keeps parallel runs stable.
- **Check the numbers, not just the page**: the checkout test recomputes the totals from the product prices.
