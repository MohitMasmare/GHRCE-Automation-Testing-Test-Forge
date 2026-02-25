package com.myntra.testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

public class SearchUpdateVerifyTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.myntra.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void searchUpdateVerify() throws InterruptedException {

        // Close Login Popup if appears
        try {
            driver.findElement(By.xpath("//span[@class='myntraweb-sprite desktop-iconClose sprites-headerIcon']")).click();
        } catch (Exception e) {
            System.out.println("Popup not displayed");
        }

        // 1️⃣ Search Product
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("desktop-searchBar")));
        searchBox.sendKeys("Tshirt");

        driver.findElement(By.className("desktop-submit")).click();

        // Click first product
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//li[@class='product-base'])[1]"))).click();

        // Switch to new tab
        String parent = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(parent)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // Select size (first available)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(@class,'size-buttons-size-button')])[1]"))).click();

        // Get unit price
        String priceText = driver.findElement(By.xpath("//span[@class='pdp-price']")).getText();
        priceText = priceText.replaceAll("[^0-9]", "");
        int unitPrice = Integer.parseInt(priceText);

        // Add to bag
        driver.findElement(By.xpath("//div[text()='ADD TO BAG']")).click();

        // Go to Bag
     // Click Bag icon from header
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='ADD TO BAG']")
        )).click();
     // Small wait for item to get added
        Thread.sleep(3000);

        // Directly open cart page (MOST STABLE WAY)
        driver.get("https://www.myntra.com/checkout/cart");

        // Wait for cart page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'itemContainer')]")
        ));

        // Wait for side mini cart popup
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'add-to-bag-modal')]")
        ));

        // Click GO TO BAG button inside popup
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'GO TO BAG')]")
        )).click();
                
      

        // 2️⃣ Update Quantity
        WebElement qtyDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='itemComponents-base-quantity']")));
        qtyDropdown.click();

        // Select quantity 2
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='quantity-list']//li[text()='2']"))).click();

        Thread.sleep(3000);

        // 3️⃣ Verify Pricing
        String totalPriceText = driver.findElement(By.xpath("//span[@class='priceDetail-base-discountedPrice']")).getText();
        totalPriceText = totalPriceText.replaceAll("[^0-9]", "");
        int totalPrice = Integer.parseInt(totalPriceText);

        int expectedTotal = unitPrice * 2;

        System.out.println("Unit Price: " + unitPrice);
        System.out.println("Expected Total: " + expectedTotal);
        System.out.println("Actual Total: " + totalPrice);

        Assert.assertEquals(totalPrice, expectedTotal, "Price calculation mismatch!");

    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}