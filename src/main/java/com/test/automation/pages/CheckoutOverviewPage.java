package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.utils.Log;

/**
 * Checkout, passo 2: resumo do pedido com valores (checkout-step-two.html).
 */
public class CheckoutOverviewPage extends BasePage {

    public static final String TITLE = "Checkout: Overview";

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> itemNameElements;

    @FindBy(css = "[data-test='subtotal-label']")
    private WebElement subtotalLabelElement;

    @FindBy(css = "[data-test='tax-label']")
    private WebElement taxLabelElement;

    @FindBy(css = "[data-test='total-label']")
    private WebElement totalLabelElement;

    @FindBy(css = "[data-test='finish']")
    private WebElement finishButtonElement;

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    public List<String> getItemNames() {
        return getTexts(itemNameElements);
    }

    /** "Item total: $29.99" -> 29.99 */
    public double getItemTotal() {
        return extractValue(getText(subtotalLabelElement));
    }

    /** "Tax: $2.40" -> 2.40 */
    public double getTax() {
        return extractValue(getText(taxLabelElement));
    }

    /** "Total: $32.39" -> 32.39 */
    public double getTotal() {
        return extractValue(getText(totalLabelElement));
    }

    public CheckoutCompletePage finish() {
        Log.info("Finalizando o pedido.");
        click(finishButtonElement);
        return new CheckoutCompletePage(driver);
    }

    private double extractValue(String label) {
        return Double.parseDouble(label.substring(label.indexOf('$') + 1));
    }
}
