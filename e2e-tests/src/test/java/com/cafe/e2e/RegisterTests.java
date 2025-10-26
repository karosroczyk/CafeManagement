package com.cafe.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterTests {
    private WebDriver driver;
    private WebDriverWait wait;

    private String loginUrl = "http://localhost:8080/login/";
    private String registerUrl = "http://localhost:8080/register/";

    @BeforeEach
    void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Register
        driver.get(registerUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @ParameterizedTest
    @CsvSource({
            "'', Doe, john@example.com, Password123, Firstname is mandatory",
            "John, '', john@example.com, Password123, Lastname is mandatory",
            "John, Doe, invalid-email, Password123, Email is not well formatted",
            "John, Doe, john@example.com, 123, Password should be 8 characters long minimum"
    })
    void shouldFailToRegisterWhenPasswordIsMissing(String firstName, String lastName, String email, String password, String expectedError) {
        driver.findElement(By.id("first_name")).sendKeys(firstName);
        driver.findElement(By.id("last_name")).sendKeys(lastName);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);

        WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn[style*='background-color']")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", registerButton);

        try {
            registerButton.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);
        }

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert.alert-danger")
        ));

        String alertText = alert.getText();

        assertThat(alertText)
                .withFailMessage("Got: %s", alertText)
                .contains(expectedError);
    }

    @ParameterizedTest
    @CsvSource({
            "employee@gmail.com, Wrong123!",
            "wrong@gmail.com, Test123!"
    })
    void invalidLogin_showsErrorMessage(String email, String password) {
        driver.get(loginUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = driver.findElement(By.id("login"));
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement loginButton = driver.findElement(By.cssSelector(".btn"));
        loginButton.click();

        WebElement errorAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger"))
        );

        assertThat(errorAlert.isDisplayed()).isTrue();
        assertThat(errorAlert.getText()).contains("Login failed. Please try again.");

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/login");
    }
}
