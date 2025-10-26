package com.cafe.e2e;

//import com.cafe.auth.userManagement.dao.UserDAOJPA;
//import com.cafe.auth.userManagement.entity.User;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // app runs on port
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
//@Import(LoginE2ETest.TestMailConfig.class)
public class FrontendUITest {
    private WebDriver driver;
    private WebDriverWait wait;

//    @Autowired
//    private UserDAOJPA userDAOJPA;

    private String loginUrl = "http://localhost:8080/login/";
    private String registerUrl = "http://localhost:8080/register";

    @BeforeEach
    void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Register
//        driver.get(registerUrl);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        driver.findElement(By.id("first_name")).sendKeys("Client");
//        driver.findElement(By.id("last_name")).sendKeys("Client");
//        driver.findElement(By.id("email")).sendKeys("client2@test.com");
//        driver.findElement(By.id("password")).sendKeys("Test1234!");
//
//        WebElement registerButton = driver.findElement(By.cssSelector(".btn"));
//        registerButton.click();
//
//        wait.until(ExpectedConditions.urlContains("/activate-account"));
//
//        String currentUrl = driver.getCurrentUrl();
//        assertThat(currentUrl).contains("/activate-account");
//
//        User u = userDAOJPA.findByEmail("client2@test.com").orElseThrow();
//        u.setEnabled(true);
//        userDAOJPA.save(u);

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

//    @TestConfiguration
//    public static class TestMailConfig {   // 🔑 make it static
//
//        @Bean
//        public JavaMailSender javaMailSender() {
//            return Mockito.mock(JavaMailSender.class);
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


//    @ParameterizedTest
//    @CsvSource({
//            "'', Doe, john@example.com, Password123, Firstname is mandatory",
//            "John, '', john@example.com, Password123, Lastname is mandatory",
//            "John, Doe, invalid-email, Password123, Email is not well formatted",
//            "John, Doe, john@example.com, 123, Password should be 8 characters long minimum"
//    })
//    void failedRegistration_showsErrorMessage(String firstName, String lastName, String email, String password, String expectedError) {
//        driver.get(registerUrl);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        driver.findElement(By.id("first_name")).sendKeys(firstName);
//        driver.findElement(By.id("last_name")).sendKeys(lastName);
//        driver.findElement(By.id("email")).sendKeys(email);
//        driver.findElement(By.id("password")).sendKeys(password);
//
//        // Click register
//        WebElement registerButton = driver.findElement(By.cssSelector(".btn"));
//        registerButton.click();
//
//        // Wait for error alert
//        WebElement errorAlert = wait.until(
//                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger"))
//        );
//
//        // Assert alert shows validation errors
//        assertThat(errorAlert.isDisplayed()).isTrue();
//        assertThat(errorAlert.getText()).contains(expectedError);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "employee@gmail.com, Wrong123!",
//            "wrong@gmail.com, Test123!"
//    })
//    void invalidLogin_showsErrorMessage(String email, String password) {
//        driver.get(loginUrl);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement emailInput = driver.findElement(By.id("login"));
//        emailInput.clear();
//        emailInput.sendKeys(email);
//
//        WebElement passwordInput = driver.findElement(By.id("password"));
//        passwordInput.clear();
//        passwordInput.sendKeys(password);
//
//        WebElement loginButton = driver.findElement(By.cssSelector(".btn"));
//        loginButton.click();
//
//        WebElement errorAlert = wait.until(
//                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger"))
//        );
//
//        assertThat(errorAlert.isDisplayed()).isTrue();
//        assertThat(errorAlert.getText()).contains("Login failed. Please try again."); // adjust to your backend error message
//
//        String currentUrl = driver.getCurrentUrl();
//        assertThat(currentUrl).contains("/login");
//    }
}