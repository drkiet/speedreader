package com.drkiettran.tools.speedreader.util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class CommonUtils {

	private static final String THE_FREE_DICTIONARY_WEBSITE = "https://www.thefreedictionary.com/";
	private static final String XPATH_SEARCH_TEXT = "//input[@type='search']";
	private static final String XPATH_SEARCH_BUTTON = "//input[@type='submit' and @value='Search']";
	private static final String XPATH_BASIC_DEFINITION = "//div[@class='pseg']";
	private static final String XPATH_SINGLE_BASIC_DEFINITION = "//div[@class='ds-single']";

	public static String getDefinitionForWord(String word) {
		WebDriver driver = new HtmlUnitDriver();
		driver.get(THE_FREE_DICTIONARY_WEBSITE);
		driver.findElement(By.xpath(XPATH_SEARCH_TEXT)).sendKeys(word);
		driver.findElement(By.xpath(XPATH_SEARCH_BUTTON)).click();
		List<WebElement> defElmts = driver.findElements(By.xpath(XPATH_BASIC_DEFINITION));
		String def = "*** no definition ***";

		if (defElmts.isEmpty()) {
			defElmts = driver.findElements(By.xpath(XPATH_SINGLE_BASIC_DEFINITION));
			if (!defElmts.isEmpty()) {
				def = "<a href=\"" + driver.getCurrentUrl() + "\">" + driver.getCurrentUrl() + "</a><br>";
				def += defElmts.get(0).getText();
			}
		} else {
			def = "<a href=\"" + driver.getCurrentUrl() + "\">" + driver.getCurrentUrl() + "</a><br>";
			def += defElmts.get(0).getText();
		}
		def = def.replaceAll("\n", "<br>");
		def = def.replaceAll("1.", "<br>1.");
		StringBuilder sb = new StringBuilder(String.format("<b>%s</b>: <br>", word)).append(def);
		return sb.toString();
	}

}
