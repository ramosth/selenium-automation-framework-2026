package com.test.automation.tests.checkout;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.dataproviders.CheckoutDataProvider;
import com.test.automation.pages.CheckoutCompletePage;
import com.test.automation.pages.CheckoutInformationPage;
import com.test.automation.pages.CheckoutOverviewPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: finalizar a compra (checkout), incluindo a validação dos campos obrigatórios.
 * Dados: CheckoutData.xlsx, aba "Checkout".
 *
 * Mesmo padrão do login: expectedResult = SUCCESS (pedido concluído) ou ERROR (mensagem de validação).
 */
public class CheckoutTest extends BaseTest {

    private static final String PRODUCT = "Sauce Labs Backpack";

    @Test(dataProvider = "checkoutExcel", dataProviderClass = CheckoutDataProvider.class)
    public void testCheckout(String testId, String scenario, String firstName, String lastName, String postalCode,
            String expectedResult, String expectedMessage) {
        Log.info("Iniciando " + testId + " - " + scenario);
        test = ExtentReportManager.createTest(testId + " - " + scenario);

        // Pré-condição: usuário logado, com um produto no carrinho, na tela de dados do checkout
        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        inventoryPage.addProductToCart(PRODUCT);
        CheckoutInformationPage informationPage = inventoryPage.header().openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");

        informationPage.fillInformation(firstName, lastName, postalCode);
        test.info("Dados informados: '" + firstName + "' / '" + lastName + "' / '" + postalCode + "'");

        switch (expectedResult.trim().toUpperCase()) {
            case "SUCCESS" -> validateCompletedOrder(informationPage, expectedMessage);
            case "ERROR" -> validateFieldError(informationPage, expectedMessage);
            default -> throw new IllegalArgumentException(
                    "Valor inválido para expectedResult: '" + expectedResult + "'. Use SUCCESS ou ERROR.");
        }
    }

    private void validateCompletedOrder(CheckoutInformationPage informationPage, String expectedMessage) {
        CheckoutOverviewPage overviewPage = informationPage.continueToOverview();
        Assert.assertTrue(overviewPage.isLoaded(), "A tela de resumo do pedido não foi exibida.");
        Assert.assertTrue(overviewPage.getItemNames().contains(PRODUCT), "O produto não aparece no resumo do pedido.");

        CheckoutCompletePage completePage = overviewPage.finish();
        Assert.assertTrue(completePage.isLoaded(), "A tela de pedido concluído não foi exibida.");
        Assert.assertEquals(completePage.getConfirmationMessage(), expectedMessage,
                "Mensagem de confirmação diferente da esperada.");
        test.pass("Pedido concluído: " + expectedMessage);
    }

    private void validateFieldError(CheckoutInformationPage informationPage, String expectedMessage) {
        informationPage.clickContinue();
        Assert.assertTrue(informationPage.isErrorMessageDisplayed(), "Era esperada uma mensagem de erro.");
        Assert.assertEquals(informationPage.getErrorMessage(), expectedMessage,
                "Mensagem de erro diferente da esperada.");
        Assert.assertTrue(informationPage.isLoaded(), "Era esperado continuar na tela de dados do checkout.");
        test.pass("Mensagem de validação correta: " + expectedMessage);
    }
}
