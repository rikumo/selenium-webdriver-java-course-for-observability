package base;

import com.google.common.io.Files;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
//import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.HomePage;
import utils.CookieManager;
import utils.EventReporter;
import utils.WindowManager;

import java.io.File;
import java.io.IOException;

public class BaseTests {

//    private EventFiringWebDriver driver;
    private WebDriver driver;
    protected HomePage homePage;


//    @BeforeClass
//    public void setUp(){
//        var driverExtention = "";
//        if(System.getenv("RUNNER_OS") != null) {
//            driverExtention = "-linux";
//        };
//        System.setProperty("webdriver.chrome.driver", "resources/chromedriver" + driverExtention);
//        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
//        driver.register(new EventReporter());
//    }

    @BeforeClass
    public void setUp(){
//        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
//        driver.register(new EventReporter());
        driver = new EventFiringDecorator<>(new EventReporter()).decorate(new ChromeDriver(getChromeOptions()));
        logChrome();
    }

    @BeforeMethod
    public void goHome(){
        driver.get("https://the-internet.herokuapp.com/");
        homePage = new HomePage(driver);
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

    @AfterMethod
    public void recordFailure(ITestResult result){
        if(ITestResult.FAILURE == result.getStatus())
        {
            var camera = (TakesScreenshot)driver;
            File screenshot = camera.getScreenshotAs(OutputType.FILE);
            try{
                File destination = new File("resources/screenshots/" + result.getName() + ".png");
                Files.createParentDirs(destination);
                Files.move(screenshot, new File("resources/screenshots/" + result.getName() + ".png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public WindowManager getWindowManager(){
        return new WindowManager(driver);
    }

    private ChromeOptions getChromeOptions(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.setBrowserVersion("153");
//        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

//        var headless = Boolean.parseBoolean(System.getenv("HEADLESS_CHROME")) | false;
//        options.setHeadless(headless);

        // Default headless mode off, set to true based on env var
        boolean headless = Boolean.parseBoolean(System.getenv("HEADLESS_CHROME"));
        if(headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private void logChrome() {
        Capabilities capabilities = ((ChromeDriver) driver).getCapabilities();
        System.out.println("Browser name: " + capabilities.getBrowserName());
        System.out.println("Browser version: " + capabilities.getBrowserVersion());
        System.out.println("Chrome driver version: " + capabilities.getCapability("chrome").toString());
    }

    public CookieManager getCookieManager(){
        return new CookieManager(driver);
    }
}
