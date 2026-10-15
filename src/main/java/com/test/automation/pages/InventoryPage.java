package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.utils.Log;

/**
 * Página de produtos (inventory.html), exibida logo após o login.
 */
public class InventoryPage extends BasePage {

    public static final String TITLE = "Products";

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> productNameElements;

    @FindBy(css = "[data-test='shopping-cart-link']")
    private WebElement cartLinkElement;

    // O contador do carrinho só existe quando há itens; por isso é buscado com
    // findElements
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    public List<String> getProductNames() {
        return getTexts(productNameElements);
    }

    // Localizador do botão "Add to cart"/"Remove" do card do produto com o nome
    // informado
    private By productButton(String productName) {
        return By.xpath(
                "//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                        + productName + "']]//button");
    }

    public void addProductToCart(String productName) {
        Log.info("Adicionando ao carrinho: " + productName);
        click(productButton(productName));
    }

    public void removeProductFromCart(String productName) {
        Log.info("Removendo do carrinho: " + productName);
        click(productButton(productName));
    }

    /** Número exibido no ícone do carrinho (0 quando o contador não aparece). */
    public int getCartBadgeCount() {
        List<WebElement> badge = driver.findElements(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    public CartPage openCart() {
        Log.info("Abrindo o carrinho.");
        click(cartLinkElement);
        return new CartPage(driver);
    }
}
