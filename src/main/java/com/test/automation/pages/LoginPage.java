package com.test.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.test.automation.utils.Log;

public class LoginPage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton   = By.id("login-button");
    public final By errorMessage  = By.cssSelector("[data-test='error']");
    private final By pageTitle     = By.className("title");

    // sempre que um objeto é criado para esta classe, o construtor é chamado por padrão
    // sempre que um objeto for criado a partir da classe da página de login, o usuário será obrigado a passar uma instância do WebDriver
    public LoginPage(WebDriver driver) {
        Log.info("Inicializando a página de login com o WebDriver fornecido.");
        this.driver = driver;
        Log.info("Configurando o WebDriverWait com um tempo de espera de 10 segundos.");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String username) {
        Log.info("Inserindo nome de usuário no campo de entrada" + usernameInput.toString() + ".");
        clearElement(usernameInput); // Limpa o campo antes de inserir o nome de usuário
        driver.findElement(usernameInput).sendKeys(username);
    }

    public void enterPassword(String password) {
        Log.info("Inserindo senha no campo de entrada: " + passwordInput.toString() + ".");
        clearElement(passwordInput); // Limpa o campo antes de inserir a senha
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLoginButton() {
        Log.info("Clicando no botão de login.");
        driver.findElement(loginButton).click();
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public WebElement waitVisible(By locator) {
        Log.info("Aguardando visibilidade do elemento: " + locator.toString());
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public String getPageTitle() {
        return waitVisible(pageTitle).getText(); // Adiciona um tempo de espera implícito para garantir que o título da página seja carregado
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoginSuccessful() {
        // Verifica se o título da página é "Products" após o login bem-sucedido
        Assert.assertTrue(getPageTitle().equals("Products"), "Login não foi bem-sucedido. Título da página: " + getPageTitle());
        return getPageTitle().equals("Products");
    }

    public boolean isErrorMessageDisplayed() {
        // Verifica se a mensagem de erro está visível na página
        return !driver.findElements(errorMessage).isEmpty();
    }

    public String getErrorMessageText() {
        // Retorna o texto da mensagem de erro, se estiver presente
        if (isErrorMessageDisplayed()) {
            return getErrorMessage();
        }
        return "";
    }

    public void clearElement(By elementLocator) {
        Log.info("Limpando o campo de entrada: " + elementLocator.toString());
        driver.findElement(elementLocator).clear();
    }

    public void checkElementVisibility(By elementLocator) {
        // Verifica se o elemento está visível na página
        boolean isVisible = !driver.findElements(elementLocator).isEmpty();
        if (!isVisible) {
            throw new RuntimeException("Elemento não está visível: " + elementLocator.toString());
        }
    }

    public void checkElementText(By elementLocator, String expectedText) {
        // Verifica se o texto do elemento corresponde ao texto esperado
        String actualText = driver.findElement(elementLocator).getText();
        if (!actualText.equals(expectedText)) {
            throw new RuntimeException("Texto do elemento não corresponde. Esperado: " + expectedText + ", Atual: " + actualText);
        }
    }

    public void errorMessageContainsText(By elementLocator, String expectedText) {
        // Verifica se a mensagem de erro contém o texto esperado
        String actualText = driver.findElement(elementLocator).getText();
        if (!actualText.contains(expectedText)) {
            throw new RuntimeException("Mensagem de erro não contém o texto esperado. Esperado: " + expectedText + ", Atual: " + actualText);
        }
    }

    public void checkElementEnabled(By elementLocator) {
        // Verifica se o elemento está habilitado na página
        boolean isEnabled = driver.findElement(elementLocator).isEnabled();
        if (!isEnabled) {
            throw new RuntimeException("Elemento não está habilitado: " + elementLocator.toString());
        }
    }

    public void checkElementDisabled(By elementLocator) {
        // Verifica se o elemento está desabilitado na página
        boolean isEnabled = driver.findElement(elementLocator).isEnabled();
        if (isEnabled) {
            throw new RuntimeException("Elemento não está desabilitado: " + elementLocator.toString());
        }
    }

    public void checkElementSelected(By elementLocator) {
        // Verifica se o elemento está selecionado na página
        boolean isSelected = driver.findElement(elementLocator).isSelected();
        if (!isSelected) {
            throw new RuntimeException("Elemento não está selecionado: " + elementLocator.toString());
        }
    }

    public void checkElementNotSelected(By elementLocator) {
        // Verifica se o elemento não está selecionado na página
        boolean isSelected = driver.findElement(elementLocator).isSelected();
        if (isSelected) {
            throw new RuntimeException("Elemento está selecionado: " + elementLocator.toString());
        }
    }

    public void checkElementAttribute(By elementLocator, String attributeName, String expectedValue) {
        // Verifica se o atributo do elemento corresponde ao valor esperado
        String actualValue = driver.findElement(elementLocator).getAttribute(attributeName);
        if (!actualValue.equals(expectedValue)) {
            throw new RuntimeException("Atributo do elemento não corresponde. Esperado: " + expectedValue + ", Atual: " + actualValue);
        }
    }
}
