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

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class UserProfileTests {
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
    void successfulUpdateUserProfile() {
        // Navigate to profile page
        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("My profile"))).click();

        wait.until(ExpectedConditions.urlContains("/cafe-client/profile"));

        // Wait for the date input to appear
        WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[name='dateOfBirth']")
        ));

        // Clear and set a new date
        dateInput.clear();
        dateInput.sendKeys("15/08/1995");

        // Click Update Profile button
        WebElement updateButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".update-btn")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", updateButton);
        wait.until(ExpectedConditions.elementToBeClickable(updateButton));

        try {
            updateButton.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", updateButton);
        }

        // Wait for success message
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert.alert-success")
        ));

        assertThat(successAlert.getText())
                .containsIgnoringCase("Profile updated successfully");
    }


}
