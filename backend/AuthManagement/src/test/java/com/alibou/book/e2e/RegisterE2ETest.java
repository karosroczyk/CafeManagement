package com.alibou.book.e2e;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // app runs on port
@ActiveProfiles("test")
@Import(RegisterE2ETest.TestMailConfig.class)
class RegisterE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        //driver.get("http://localhost:4200/register");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @TestConfiguration
    public static class TestMailConfig {   // 🔑 make it static

        @Bean
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }

    @Test
    void successfulRegistration_redirectsToLogin() {
        driver.findElement(By.id("first_name")).sendKeys("John");
        driver.findElement(By.id("last_name")).sendKeys("Doe");
        driver.findElement(By.id("email")).sendKeys("newuser" + System.currentTimeMillis() + "@test.com");
        driver.findElement(By.id("password")).sendKeys("Test123!");

        WebElement registerButton = driver.findElement(By.cssSelector(".btn"));
        registerButton.click();

        wait.until(ExpectedConditions.urlContains("/activate-account"));

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/activate-account");
    }

    @ParameterizedTest
    @CsvSource({
            "'', Doe, john@example.com, Password123, Firstname is mandatory",
            "John, '', john@example.com, Password123, Lastname is mandatory",
            "John, Doe, invalid-email, Password123, Email is not well formatted",
            "John, Doe, john@example.com, 123, Password should be 8 characters long minimum"
    })
    void failedRegistration_showsErrorMessage(String firstName, String lastName, String email, String password, String expectedError) {
        // Missing required fields -> trigger validation
        driver.findElement(By.id("first_name")).sendKeys(firstName);
        driver.findElement(By.id("last_name")).sendKeys(lastName);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);

        // Click register
        WebElement registerButton = driver.findElement(By.cssSelector(".btn"));
        registerButton.click();

        // Wait for error alert
        WebElement errorAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger"))
        );

        // Assert alert shows validation errors
        assertThat(errorAlert.isDisplayed()).isTrue();
        assertThat(errorAlert.getText()).contains(expectedError);
    }
}