package com.test.automation.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.automation.config.ConfigReader;
import com.test.automation.utils.Log;

/**
 * Classe base de todas as páginas (Page Object).
 *
 * Guarda o que é comum a qualquer página: o driver, a espera explícita (WebDriverWait),
 * a inicialização dos elementos (PageFactory) e ações genéricas (clicar, digitar, ler texto, esperar).
 * Cada página herda daqui e só declara os próprios elementos e ações.
 *
 * Regra do Page Object: a página não faz Assert. Ela executa ações e responde perguntas
 * (true/false, textos, listas); quem decide se o teste passou é a classe de teste.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Título exibido no topo das páginas internas do Sauce Demo ("Products", "Your Cart", ...)
    @FindBy(className = "title")
    protected WebElement pageTitleElement;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.get("timeout.seconds", "10"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        PageFactory.initElements(driver, this); // inicializa os elementos anotados com @FindBy
        Log.info("Página carregada: " + getClass().getSimpleName());
    }

    // ---------- Esperas ----------

    public WebElement waitVisible(WebElement element) {
        Log.info("Aguardando visibilidade do elemento: " + element);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // ---------- Ações genéricas ----------

    protected void click(WebElement element) {
        waitClickable(element).click();
    }

    /** Limpa o campo e digita o texto. */
    protected void type(WebElement element, String text) {
        clearElement(element);
        element.sendKeys(text);
    }

    public void clearElement(WebElement element) {
        Log.info("Limpando o campo de entrada: " + element);
        element.clear();
    }

    protected String getText(WebElement element) {
        return waitVisible(element).getText();
    }

    /** Textos de uma lista de elementos (ex.: nomes dos produtos). */
    protected List<String> getTexts(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /** true se o elemento estiver na tela, sem lançar exceção quando ele não existe. */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ---------- Título da página ----------

    public String getPageTitle() {
        return waitVisible(pageTitleElement).getText();
    }

    /** Espera o título da página ficar igual ao esperado; false se não acontecer no tempo limite. */
    public boolean hasTitle(String expectedTitle) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("title"), expectedTitle));
        } catch (TimeoutException e) {
            Log.warn("Título '" + expectedTitle + "' não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    // ---------- Verificações genéricas (vindas da primeira aula) ----------

    public void checkElementVisibility(WebElement element) {
        if (!element.isDisplayed()) {
            throw new RuntimeException("Elemento não está visível: " + element);
        }
    }

    public void checkElementText(WebElement element, String expectedText) {
        String actualText = element.getText();
        if (!actualText.equals(expectedText)) {
            throw new RuntimeException("Texto do elemento não corresponde. Esperado: " + expectedText + ", Atual: " + actualText);
        }
    }

    public void errorMessageContainsText(WebElement element, String expectedText) {
        String actualText = element.getText();
        if (!actualText.contains(expectedText)) {
            throw new RuntimeException("Mensagem de erro não contém o texto esperado. Esperado: " + expectedText + ", Atual: " + actualText);
        }
    }

    public void checkElementEnabled(WebElement element) {
        if (!element.isEnabled()) {
            throw new RuntimeException("Elemento não está habilitado: " + element);
        }
    }

    public void checkElementDisabled(WebElement element) {
        if (element.isEnabled()) {
            throw new RuntimeException("Elemento não está desabilitado: " + element);
        }
    }

    public void checkElementSelected(WebElement element) {
        if (!element.isSelected()) {
            throw new RuntimeException("Elemento não está selecionado: " + element);
        }
    }

    public void checkElementNotSelected(WebElement element) {
        if (element.isSelected()) {
            throw new RuntimeException("Elemento está selecionado: " + element);
        }
    }

    public void checkElementAttribute(WebElement element, String attributeName, String expectedValue) {
        String actualValue = element.getAttribute(attributeName);
        if (!expectedValue.equals(actualValue)) {
            throw new RuntimeException("Atributo do elemento não corresponde. Esperado: " + expectedValue + ", Atual: " + actualValue);
        }
    }
}
