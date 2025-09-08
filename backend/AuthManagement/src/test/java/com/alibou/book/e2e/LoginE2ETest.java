package com.alibou.book.e2e;

import com.alibou.book.userManagement.dao.UserDAOJPA;
import com.alibou.book.userManagement.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // app runs on port
@ActiveProfiles("test")
@Import(LoginE2ETest.TestMailConfig.class)
public class LoginE2ETest {
    private WebDriver driver;
    private WebDriverWait wait; 

    @Autowired
    private UserDAOJPA userDAOJPA;

    private String loginUrl = "http://localhost:4200/login";
    private String registerUrl = "http://localhost:4200/register";

    @BeforeEach
    void setUp() throws IOException {
        driver = new ChromeDriver();

        // Register
        driver.get(registerUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.findElement(By.id("first_name")).sendKeys("Client");
        driver.findElement(By.id("last_name")).sendKeys("Client");
        driver.findElement(By.id("email")).sendKeys("client@test.com");
        driver.findElement(By.id("password")).sendKeys("Test1234!");

        WebElement registerButton = driver.findElement(By.cssSelector(".btn"));
        registerButton.click();

        wait.until(ExpectedConditions.urlContains("/activate-account"));

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/activate-account");

        User u = userDAOJPA.findByEmail("client@test.com").orElseThrow();
        u.setEnabled(true);
        userDAOJPA.save(u);

        // Login
        driver.get(loginUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = driver.findElement(By.id("login"));
        emailInput.clear();
        emailInput.sendKeys("client@test.com");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("Test1234!");

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

    @TestConfiguration
    public static class TestMailConfig {   // 🔑 make it static

        @Bean
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }

    @Test
    void successfulMainPageRedirectToMenu() {
        // MainCafe
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
            String routerLink = link.getAttribute("ng-reflect-router-link");
            assertThat(routerLink).contains(expected.get(text));
        }

        WebElement logoutBtn = navbar.findElement(By.cssSelector("form button"));
        assertThat(logoutBtn.isDisplayed()).isTrue();

        //Redirect to Menu
        WebElement firstButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("app-main button:first-of-type"))
        );
        firstButton.click();
        wait.until(ExpectedConditions.urlContains("/cafe-client/menucafe"));

        String currentMenuUrl = driver.getCurrentUrl();
        assertThat(currentMenuUrl).contains("/cafe-client/menucafe");
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