package com.cafe.menumanagement.e2e;

import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Import(MenuCafeE2ETest.MockEurekaConfig.class)
class MenuCafeE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @TestConfiguration
    static class MockEurekaConfig {
        @Bean
        public EurekaClient mockEurekaClient() {
            return Mockito.mock(EurekaClient.class);
        }
    }
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("http://localhost:4200/cafe-client/menucafe");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void menuPage_rendersCategoriesAndItems() {
        WebElement emailInput = driver.findElement(By.id("login"));
        emailInput.clear();
        emailInput.sendKeys("employee@gmail.com");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("Test123!");

        WebElement loginButton = driver.findElement(By.cssSelector(".btn"));
        loginButton.click();

        //Thread.sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/cafe-employee");


        // Wait for menu page header
        WebElement menuHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-header h1"))
        );
        assertThat(menuHeader.getText()).isEqualTo("Menu");

        // Verify that categories are rendered in nav
        List<WebElement> categories = driver.findElements(By.cssSelector(".menu-nav li a"));
        assertThat(categories).isNotEmpty();

        // Verify that each category section exists
        for (WebElement categoryLink : categories) {
            String href = categoryLink.getAttribute("href");
            WebElement categorySection = driver.findElement(By.cssSelector("section" + href.substring(href.indexOf("#"))));
            assertThat(categorySection.isDisplayed()).isTrue();

            // Verify that menu items are displayed under the category
            List<WebElement> menuItems = categorySection.findElements(By.cssSelector(".menu-item"));
            assertThat(menuItems).isNotEmpty();

            for (WebElement menuItem : menuItems) {
                WebElement name = menuItem.findElement(By.tagName("h3"));
                WebElement description = menuItem.findElement(By.tagName("p"));
                WebElement price = menuItem.findElement(By.cssSelector(".price"));

                assertThat(name.getText()).isNotEmpty();
                assertThat(description.getText()).isNotEmpty();
                assertThat(price.getText()).startsWith("PLN");
            }
        }
    }
}