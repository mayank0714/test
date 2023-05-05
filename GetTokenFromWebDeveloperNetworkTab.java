package rest.helpers;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v111.network.Network;
import org.openqa.selenium.devtools.v111.network.model.Request;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import rest.executors.TestExecutor;

//import io.github.bonigarcia.wdm.WebDriverManager;

public class GetTokenFromWebDeveloperNetworkTab {

	public static WebDriver driver;
	private static String token;
	
	public static String getToken() {
		if(token!=null) {
			return token;
		}
		try {
			
			if(TestExecutor.configXml.readTagVal("Execution").contentEquals("GITHUB")) {
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--no-sandbox"); 
				options.addArguments("--headless");

				driver = new ChromeDriver(options);
			} else {
				driver = new ChromeDriver();
			}
			  
			
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			driver.navigate().to(TestExecutor.configXml.readTagVal("UrlForToken"));

			Thread.sleep(10000);
			//enterText("//input[@id='IDToken1']", "S7GW");			
			enterText("//input[@class='smart-id']", TestExecutor.configXml.readTagVal("Username"));
			enterText("//input[@id='password']", TestExecutor.configXml.readTagVal("Password"));
			click("//input[@id='button-login']");

			Thread.sleep(10000);

			DevTools devTools = ((HasDevTools) driver).getDevTools();
			devTools.createSession();
			devTools.send(Network.enable(Optional.of(5000), Optional.of(5000), Optional.of(5000)));
			devTools.addListener(Network.requestWillBeSent(), request->{

				Request req = request.getRequest(); 
				String url = req.getUrl();
				System.out.println("URL - " + url);
				if(url.contains("api.spm-dev2.com/iam-management/branchDetails/")) {
					System.out.println("Response headers: "+ req.getHeaders().toString());
					if(req.getHeaders().containsKey("Authorization")) {
						token = req.getHeaders().get("Authorization").toString();
						System.out.println("Token: "+ token);
						
					}
				}
			});

			driver.navigate().refresh();
			Thread.sleep(15000);
			driver.quit();
		}catch(Exception e) {
			e.printStackTrace();
			if(driver!=null) {
				driver.quit();
			}
		}
		return token;
	}
	
	public static void enterText(String xpath, String text) throws Exception {
		WebElement element = driver.findElement(By.xpath(xpath));
		waitforElement(element);
		element.click();
		element.sendKeys(text);
	}

	public static void click(String xpath) throws Exception {
		WebElement element = driver.findElement(By.xpath(xpath));
		waitforElement(element);
		element.click();
	}

	public static void waitforElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));    
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}
}
