package com.test.automation.tests.checkout;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.OrderScenario;
import com.test.automation.dataproviders.CheckoutDataProvider;
import com.test.automation.pages.CheckoutInformationPage;
import com.test.automation.pages.CheckoutOverviewPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: cálculo do resumo do pedido (tela Checkout: Overview).
 *   Item total = soma dos preços dos itens
 *   Tax        = 8% do item total, arredondado para 2 casas
 *   Total      = item total + tax
 *
 * Dados: order-scenarios.json, com os valores esperados já calculados em cada cenário.
 * Escolha dos cenários: 1 item mais barato e 1 item mais caro (limites de preço),
 * 2 e 3 itens, e todos os 6 produtos (limite de quantidade).
 */
public class OrderSummaryTest extends BaseTest {

    // Margem para comparar números com casas decimais (29.99 pode virar 29.989999... no computador)
    private static final double PRICE_DELTA = 0.001;

    @Test(dataProvider = "orderScenarios", dataProviderClass = CheckoutDataProvider.class)
    public void testOrderSummaryValues(OrderScenario scenario) {
        Log.info("Iniciando " + scenario.testId() + " - " + scenario.scenario());
        test = ExtentReportManager.createTest(scenario.testId() + " - " + scenario.scenario());

        // Pré-condição: usuário logado, com os produtos do cenário no carrinho
        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        for (String product : scenario.products()) {
            inventoryPage.addProductToCart(product);
        }
        test.info("Produtos adicionados: " + scenario.products());

        // Carrinho -> dados do comprador -> resumo do pedido
        CheckoutInformationPage informationPage = inventoryPage.header().openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");
        informationPage.fillInformation("Maria", "Silva", "20000-000");

        CheckoutOverviewPage overviewPage = informationPage.continueToOverview();
        Assert.assertTrue(overviewPage.isLoaded(), "A tela de resumo do pedido não foi exibida.");

        // Validações
        Assert.assertEqualsNoOrder(overviewPage.getItemNames().toArray(), scenario.products().toArray(),
                "Os itens do resumo são diferentes dos produtos adicionados.");

        test.info("Valores na tela: Item total " + overviewPage.getItemTotal()
                + " | Tax " + overviewPage.getTax() + " | Total " + overviewPage.getTotal());

        Assert.assertEquals(overviewPage.getItemTotal(), scenario.expectedItemTotal(), PRICE_DELTA,
                "Item total diferente do esperado (soma dos preços).");
        Assert.assertEquals(overviewPage.getTax(), scenario.expectedTax(), PRICE_DELTA,
                "Taxa diferente da esperada (8% do item total).");
        Assert.assertEquals(overviewPage.getTotal(), scenario.expectedTotal(), PRICE_DELTA,
                "Total diferente do esperado (item total + taxa).");

        test.pass("Item total, taxa e total conferem com o esperado.");
    }
}