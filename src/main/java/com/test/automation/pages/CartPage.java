package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.utils.Log;

/**
 * Página do carrinho (cart.html).
 */
public class CartPage extends BasePage {

    public static final String TITLE = "Your Cart";

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> itemNameElements;

    @FindBy(css = "[data-test='checkout']")
    private WebElement checkoutButtonElement;

    @FindBy(css = "[data-test='continue-shopping']")
    private WebElement continueShoppingButtonElement;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    public List<String> getItemNames() {
        return getTexts(itemNameElements);
    }

    public int getItemCount() {
        return itemNameElements.size();
    }

    public CheckoutInformationPage goToCheckout() {
        Log.info("Clicando em Checkout.");
        click(checkoutButtonElement);
        return new CheckoutInformationPage(driver);
    }

    public InventoryPage continueShopping() {
        click(continueShoppingButtonElement);
        return new InventoryPage(driver);
    }
}
