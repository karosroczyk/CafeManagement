package com.cafe.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class FrontendUITest {
    private WebDriver driver;
    private WebDriverWait wait;

    private String loginUrl = "http://localhost:8080/login/";

    @BeforeEach
    void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Login
        driver.get(loginUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = driver.findElement(By.id("login"));
        emailInput.clear();
        emailInput.sendKeys("wert@gmail.com");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("Test123!");

        WebElement loginButton = driver.findElement(By.cssSelector(".btn"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/cafe-client"));

        String currentHomeUrl = driver.getCurrentUrl();
        assertThat(currentHomeUrl).contains("/cafe-client");
    }

//    @AfterEach
//    void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }

    @Test
    void successfulMainPage() {
        WebElement navbar = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("nav.custom-navbar"))
        );

        WebElement brand = navbar.findElement(By.cssSelector(".navbar-brand"));
        assertThat(brand.getText()).isEqualTo("Cafe");

        List<WebElement> links = navbar.findElements(By.cssSelector(".navbar-nav .nav-link"));
        assertThat(links).hasSizeGreaterThanOrEqualTo(5);

        Map<String,String> expected = Map.of(
                "Home", "/cafe-client",
                "Cafe Menu", "/cafe-client/menucafe",
                "Create an order", "/cafe-client/order",
                "My orders", "/cafe-client/orderHistory",
                "My profile", "/cafe-client/profile"
        );

        for (WebElement link : links) {
            String text = link.getText().trim();
            String routerLink = link.getAttribute("href");
            assertThat(routerLink).contains(expected.get(text));
        }

        WebElement logoutBtn = navbar.findElement(By.cssSelector("form button"));
        assertThat(logoutBtn.isDisplayed()).isTrue();

        // Check second button navigates to 'Create an order'
        WebElement secondButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("app-main button:nth-of-type(2)"))
        );
        secondButton.click();

        wait.until(ExpectedConditions.urlContains("/cafe-client/order"));

        String currentOrderUrl = driver.getCurrentUrl();
        assertThat(currentOrderUrl).contains("/cafe-client/order");
    }

    @Test
    void successfulRedirectToMenuByButtonAndCheckMenu() {
        WebElement firstButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("app-main button:first-of-type"))
        );
        firstButton.click();

        wait.until(ExpectedConditions.urlContains("/cafe-client/menucafe"));

        String currentMenuUrl = driver.getCurrentUrl();
        assertThat(currentMenuUrl).contains("/cafe-client/menucafe");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-page")));

        List<WebElement> categoryHeaders = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".menu-section h2"), 0)
        );

        List<String> categoryNames = categoryHeaders.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();

        assertThat(categoryNames)
                .containsExactlyInAnyOrder("Cakes", "Pastries");

        WebElement cakesSection = categoryHeaders.stream()
                .filter(h2 -> h2.getText().trim().equals("Cakes"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Cakes category not found"))
                .findElement(By.xpath("./ancestor::section"));

        List<WebElement> cakeItems = cakesSection.findElements(By.cssSelector(".menu-item h3"));
        List<String> cakeItemNames = cakeItems.stream()
                .map(WebElement::getText)
                .map(text -> text.replaceAll("\\s+", " ").trim())
                .toList();

        assertThat(cakeItemNames)
                .containsExactlyInAnyOrder("Carrot Cake", "Cheesecake");
    }

    @Test
    void successfulCreateOrderAndCheckMyOrders() {
        // Navigate to the order page
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/cafe-client/order']"))).click();

        wait.until(ExpectedConditions.urlContains("/cafe-client/order"));
        assertThat(driver.getCurrentUrl()).contains("/cafe-client/order");

        // Wait for the menu to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-page")));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".menu-section"), 0));

        // Find "Carrot Cake" menu item
        WebElement carrotCakeItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'menu-item')]//h3[contains(.,'Carrot Cake')]")
        ));
        WebElement carrotCakeSection = carrotCakeItem.findElement(By.xpath("./ancestor::div[contains(@class,'menu-item')]"));

        // Increase quantity by 2
        WebElement plusButton = carrotCakeSection.findElement(By.xpath(".//button[contains(text(), '+')]"));
        plusButton.click();
        plusButton.click();

        // Click "Add to Basket"
        WebElement addToBasketButton = carrotCakeSection.findElement(By.xpath(".//button[contains(text(),'Add to Basket')]"));
        addToBasketButton.click();

        // Verify item appears in Basket
        WebElement basketItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//section[contains(@class,'basket')]//div[contains(@class,'basket-item')]//h3[contains(.,'Carrot Cake')]")
        ));
        assertThat(basketItem.getText()).contains("Carrot Cake");

        // Click "Place Order"
        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".place-order-btn")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", placeOrderBtn);
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn));

        try {
            placeOrderBtn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", placeOrderBtn);
        }

        // Navigate manually to 'My orders'
        WebElement myOrdersLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("My orders")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", myOrdersLink);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", myOrdersLink);

        wait.until(ExpectedConditions.urlContains("/cafe-client/orderHistory"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-table")));

        // Verify that at least 1 item appears
        boolean orderFound = wait.until(driver -> {
            List<WebElement> rows = driver.findElements(By.cssSelector(".order-table tbody tr"));
            return !rows.isEmpty();
        });

        assertThat(orderFound)
                .withFailMessage("No orders found in 'My orders' table after placing an order")
                .isTrue();

        // Find all orders and verify the latest
        List<WebElement> orders = driver.findElements(By.cssSelector(".order-table tbody tr"));

        WebElement latestOrder = orders.get(orders.size() - 1);
        String status = latestOrder.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
        assertThat(status)
                .withFailMessage("Order status should not be empty")
                .isNotEmpty();

        // Expand and check details
        driver.findElement(By.tagName("body")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".cdk-overlay-backdrop.cdk-overlay-backdrop-showing")
        ));

        // Get the details inside the latest order
        WebElement detailsSummary = latestOrder.findElement(By.cssSelector("summary"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", detailsSummary);
        wait.until(ExpectedConditions.elementToBeClickable(detailsSummary));

        try {
            detailsSummary.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailsSummary);
        }

        // Wait for details to appear
        wait.until(driver -> {
            List<WebElement> details = latestOrder.findElements(By.cssSelector(".order-details ul li p strong"));
            return !details.isEmpty();
        });

        // Check the details are not empty
        List<WebElement> items = latestOrder.findElements(By.cssSelector(".order-details ul li p strong"));
        assertThat(items)
                .withFailMessage("Order details should list at least one item")
                .isNotEmpty();
    }
}