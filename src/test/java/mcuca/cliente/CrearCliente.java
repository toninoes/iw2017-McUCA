package mcuca.cliente;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class CrearCliente {
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
  public void testCrearCliente() throws Exception {
    driver.get(baseUrl + "/cliente");
    driver.findElement(By.cssSelector("div.v-button.v-widget")).click();
    driver.findElement(By.id("gwt-uid-3")).clear();
    driver.findElement(By.id("gwt-uid-3")).sendKeys("Antonio");
    driver.findElement(By.id("gwt-uid-5")).clear();
    driver.findElement(By.id("gwt-uid-5")).sendKeys("Martinez");
    driver.findElement(By.id("gwt-uid-7")).clear();
    driver.findElement(By.id("gwt-uid-7")).sendKeys("calle ikea");
    driver.findElement(By.id("gwt-uid-9")).clear();
    driver.findElement(By.id("gwt-uid-9")).sendKeys("674567823");
    driver.findElement(By.xpath("//div[@id='ROOT-2521314']/div/div[2]/div[5]/div/div[3]/div/div[11]/div/div")).click();
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
