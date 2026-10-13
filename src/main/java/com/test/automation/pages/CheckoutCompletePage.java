package com.test.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Checkout, passo final: confirmação do pedido (checkout-complete.html).
 */
public class CheckoutCompletePage extends BasePage {

    public static final String TITLE = "Checkout: Complete!";

    @FindBy(css = "[data-test='complete-header']")
    private WebElement completeHeaderElement;

    @FindBy(css = "[data-test='back-to-products']")
    private WebElement backHomeButtonElement;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    /** "Thank you for your order!" */
    public String getConfirmationMessage() {
        return getText(completeHeaderElement);
    }

    public InventoryPage backHome() {
        click(backHomeButtonElement);
        return new InventoryPage(driver);
    }
}
