package com.nidasehba.ui.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.nidasehba.ui.config.Config;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * One Playwright instance and browser per test thread, a fresh context per test so tests never share
 * cookies or cart state. On failure a screenshot and a Playwright trace are saved under target/.
 */
public abstract class BaseUiTest {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final Queue<Playwright> ALL_INSTANCES = new ConcurrentLinkedQueue<>();
    private final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private final ThreadLocal<Page> page = new ThreadLocal<>();

    protected Page page() {
        return page.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void openBrowser() {
        if (BROWSER.get() == null) {
            Playwright playwright = Playwright.create();
            playwright.selectors().setTestIdAttribute("data-test");
            PLAYWRIGHT.set(playwright);
            ALL_INSTANCES.add(playwright);
            BROWSER.set(browserType(playwright).launch(launchOptions()));
        }
        BrowserContext ctx = BROWSER.get().newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.baseUrl())
                .setViewportSize(1366, 900));
        ctx.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
        context.set(ctx);
        page.set(ctx.newPage());
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext(ITestResult result) {
        BrowserContext ctx = context.get();
        if (ctx == null) {
            return;
        }
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getName()
                + "-" + System.currentTimeMillis();
        if (!result.isSuccess()) {
            page.get().screenshot(new Page.ScreenshotOptions()
                    .setPath(artifact("screenshots", name + ".png")).setFullPage(true));
            ctx.tracing().stop(new Tracing.StopOptions().setPath(artifact("traces", name + ".zip")));
        } else {
            ctx.tracing().stop();
        }
        ctx.close();
        context.remove();
        page.remove();
    }

    @AfterSuite(alwaysRun = true)
    public void closeBrowsers() {
        Playwright playwright;
        while ((playwright = ALL_INSTANCES.poll()) != null) {
            playwright.close();
        }
    }

    private static BrowserType browserType(Playwright playwright) {
        switch (Config.browser()) {
            case "firefox":
                return playwright.firefox();
            case "webkit":
                return playwright.webkit();
            default:
                return playwright.chromium();
        }
    }

    private static BrowserType.LaunchOptions launchOptions() {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(Config.headless());
        if (!Config.channel().isEmpty()) {
            options.setChannel(Config.channel());
        }
        return options;
    }

    private static Path artifact(String folder, String file) {
        return Paths.get("target", folder, file);
    }
}
