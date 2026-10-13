package com.test.automation.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.config.ConfigReader;
import com.test.automation.utils.Log;

/**
 * Página de login (https://www.saucedemo.com/).
 */
public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameInputElement;

    @FindBy(id = "password")
    private WebElement passwordInputElement;

    @FindBy(id = "login-button")
    private WebElement loginButtonElement;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessageElement;

    // Sempre que um objeto desta página é criado, o teste precisa passar o WebDriver.
    // O construtor da BasePage cuida do WebDriverWait e do PageFactory.
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUsername(String username) {
        Log.info("Inserindo nome de usuário: '" + username + "'");
        type(usernameInputElement, username);
    }

    public void enterPassword(String password) {
        Log.info("Inserindo senha.");
        type(passwordInputElement, password);
    }

    public void clickLoginButton() {
        Log.info("Clicando no botão de login.");
        click(loginButtonElement);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Login com o usuário padrão do config.properties (standard.username / standard.password).
     * Usado como pré-condição pelos testes de carrinho e checkout. Devolve a página de produtos.
     */
    public InventoryPage loginAsStandardUser() {
        login(ConfigReader.get("standard.username", "standard_user"),
                ConfigReader.get("standard.password", "secret_sauce"));
        return new InventoryPage(driver);
    }

    // Login bem-sucedido = o título "Products" aparece após o login
    public boolean isLoginSuccessful() {
        try {
            return getPageTitle().equals("Products");
        } catch (TimeoutException e) {
            Log.warn("Título da página de produtos não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    public String getErrorMessage() {
        return getText(errorMessageElement);
    }

    // Se a mensagem não aparecer no tempo de espera, retorna false
    public boolean isErrorMessageDisplayed() {
        try {
            return waitVisible(errorMessageElement).isDisplayed();
        } catch (TimeoutException e) {
            Log.warn("Mensagem de erro não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    public String getErrorMessageText() {
        return isErrorMessageDisplayed() ? getErrorMessage() : "";
    }

    // Continuar na tela de login = o botão de login ainda está visível
    public boolean isOnLoginPage() {
        return isDisplayed(loginButtonElement);
    }
}
