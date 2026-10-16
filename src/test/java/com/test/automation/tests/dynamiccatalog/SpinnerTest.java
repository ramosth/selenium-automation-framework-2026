package com.test.automation.tests.dynamiccatalog;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.Product;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.SpinnerPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: Dynamic Catalog > Spinner.
 * A página mostra um spinner por um tempo ALEATÓRIO (0,5 a 3 segundos) e depois a grade
 * com os 6 produtos. Por isso os testes usam espera explícita (esperar o spinner sumir)
 * e nunca Thread.sleep: um tempo fixo falharia sempre que o carregamento demorasse mais.
 */
public class SpinnerTest extends BaseTest {

    private static final double PRICE_DELTA = 0.001;

    /** Pré-condição: usuário padrão logado, na página do Spinner (aberta pelo menu). */
    private SpinnerPage openSpinnerPage(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        SpinnerPage spinnerPage = inventoryPage.header().goToSpinner();
        Assert.assertTrue(spinnerPage.isLoaded(), "A página do Spinner não foi exibida.");
        return spinnerPage;
    }

    @Test
    public void testSpinnerShownWhileLoading() {
        SpinnerPage spinnerPage = openSpinnerPage("SPN01 - Spinner aparece enquanto os produtos carregam");

        Assert.assertTrue(spinnerPage.waitForSpinnerToAppear(), "O spinner de carregamento não apareceu.");
        Assert.assertTrue(spinnerPage.waitForProductsToLoad(), "O spinner não sumiu ou os produtos não apareceram.");
        Assert.assertFalse(spinnerPage.isSpinnerDisplayed(), "O spinner continua na tela depois do carregamento.");
        test.pass("O spinner aparece durante o carregamento e some quando os produtos são exibidos.");
    }

    @Test
    public void testAllProductsShownAfterLoading() {
        SpinnerPage spinnerPage = openSpinnerPage("SPN02 - Todos os produtos aparecem após o carregamento");

        Assert.assertTrue(spinnerPage.waitForProductsToLoad(), "O spinner não sumiu ou os produtos não apareceram.");
        Assert.assertEquals(spinnerPage.getProductCount(), Product.values().length,
                "Quantidade de produtos na grade diferente do catálogo.");
        test.pass("Os " + Product.values().length + " produtos aparecem após o carregamento.");
    }

    @Test
    public void testProductNamesAndPrices() {
        SpinnerPage spinnerPage = openSpinnerPage("SPN03 - Nome e preço de cada produto");
        Assert.assertTrue(spinnerPage.waitForProductsToLoad(), "O spinner não sumiu ou os produtos não apareceram.");

        // O enum Product tem o valor esperado de cada produto: id (usado no data-test), nome e preço
        for (Product product : Product.values()) {
            Assert.assertEquals(spinnerPage.getProductName(product.getId()), product.getName(),
                    "Nome errado no card de id " + product.getId());
            Assert.assertEquals(spinnerPage.getProductPrice(product.getId()), product.getPrice(), PRICE_DELTA,
                    "Preço errado para " + product.getName());
        }
        test.pass("Nome e preço dos " + Product.values().length + " produtos conferem com o catálogo.");
    }

    @Test
    public void testSpinnerShownAgainAfterReload() {
        SpinnerPage spinnerPage = openSpinnerPage("SPN04 - Spinner aparece de novo ao recarregar a página");
        Assert.assertTrue(spinnerPage.waitForProductsToLoad(), "O carregamento inicial não terminou.");

        spinnerPage.refreshPage();

        Assert.assertTrue(spinnerPage.waitForSpinnerToAppear(), "O spinner não apareceu ao recarregar a página.");
        Assert.assertTrue(spinnerPage.waitForProductsToLoad(), "Os produtos não apareceram depois de recarregar.");
        Assert.assertEquals(spinnerPage.getProductCount(), Product.values().length,
                "Quantidade de produtos diferente depois de recarregar.");
        test.pass("Ao recarregar, o spinner aparece de novo e os produtos voltam a ser exibidos.");
    }
}