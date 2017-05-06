package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class TestFuncionalCliente {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8181/cliente";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testFuncionalCliente() throws Exception {
    driver.get(baseUrl + "/cliente");
    driver.findElement(By.cssSelector("div.v-button.v-widget")).click();
    driver.findElement(By.id("gwt-uid-3")).clear();
    driver.findElement(By.id("gwt-uid-3")).sendKeys("Manuel");
    driver.findElement(By.id("gwt-uid-5")).clear();
    driver.findElement(By.id("gwt-uid-5")).sendKeys("Padilla");
    driver.findElement(By.id("gwt-uid-7")).clear();
    driver.findElement(By.id("gwt-uid-7")).sendKeys("Calle 1");
    driver.findElement(By.id("gwt-uid-9")).clear();
    driver.findElement(By.id("gwt-uid-9")).sendKeys("657342354");
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
