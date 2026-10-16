package com.test.automation.tests.inventory;

import org.testng.Assert;

import com.test.automation.base.BaseTest;
import com.test.automation.data.ProductScenario;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.ProductDetailsPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Base dos testes de detalhes do produto: concentra as validações, para que
 * ProductDetailsTest e ProductDetailsUsersTest usem exatamente a mesma regra
 * (mesma ideia do BaseLoginTest).
 */
public abstract class BaseProductDetailsTest extends BaseTest {

    // Margem para comparar números com casas decimais (29.99 pode virar 29.989999... no computador)
    private static final double PRICE_DELTA = 0.001;

    /** Confere preço na lista, id na URL, nome, descrição, preço e imagem na página de detalhes. */
    protected void validateProductDetails(ProductScenario scenario) {
        InventoryPage inventoryPage = login("Detalhes conferem com o esperado", scenario);

        Assert.assertEquals(inventoryPage.getProductPrice(scenario.productName()), scenario.expectedPrice(),
                PRICE_DELTA, "Preço na lista de produtos diferente do esperado.");

        ProductDetailsPage detailsPage = openDetails(inventoryPage, scenario);

        Assert.assertEquals(detailsPage.getProductIdFromUrl(), scenario.expectedId(),
                "A página aberta é de outro produto (id na URL).");
        Assert.assertEquals(detailsPage.getProductName(), scenario.productName(), "Nome diferente do esperado.");
        Assert.assertEquals(detailsPage.getProductDescription(), scenario.expectedDescription(),
                "Descrição diferente da esperada.");
        Assert.assertEquals(detailsPage.getProductPrice(), scenario.expectedPrice(), PRICE_DELTA,
                "Preço na página de detalhes diferente do esperado.");
        Assert.assertTrue(detailsPage.isProductImageVisible(), "A imagem do produto não está visível.");
        test.pass("Os detalhes do produto conferem com o esperado.");
    }

    /** Adiciona e remove o produto pela página de detalhes e confere botão e contador do carrinho. */
    protected void validateAddAndRemove(ProductScenario scenario) {
        InventoryPage inventoryPage = login("Adicionar e remover pela página de detalhes", scenario);
        ProductDetailsPage detailsPage = openDetails(inventoryPage, scenario);

        detailsPage.addToCart();
        Assert.assertTrue(detailsPage.isRemoveButtonVisible(), "O botão 'Remove' não apareceu após adicionar.");
        Assert.assertEquals(detailsPage.header().getCartBadgeCount(), 1, "O carrinho deveria ter 1 item.");

        detailsPage.removeFromCart();
        Assert.assertFalse(detailsPage.isRemoveButtonVisible(), "O botão 'Remove' continuou após remover.");
        Assert.assertEquals(detailsPage.header().getCartBadgeCount(), 0, "O carrinho deveria estar vazio.");

        InventoryPage backPage = detailsPage.backToProducts();
        Assert.assertTrue(backPage.isLoaded(), "Não voltou para a página de produtos.");
        test.pass("Produto adicionado e removido pela página de detalhes.");
    }

    /** Pré-condição: login com o usuário do cenário e registro no relatório. */
    private InventoryPage login(String testName, ProductScenario scenario) {
        String fullName = scenario.testId() + " - " + testName + " - " + scenario.scenario();
        Log.info("Iniciando " + fullName);
        test = ExtentReportManager.createTest(fullName);
        test.info("Usuário: " + scenario.user() + " | Produto: " + scenario.productName());

        InventoryPage inventoryPage = new LoginPage(driver).loginAs(scenario.user());
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
        return inventoryPage;
    }

    /** Abre a página de detalhes do produto do cenário. */
    private ProductDetailsPage openDetails(InventoryPage inventoryPage, ProductScenario scenario) {
        ProductDetailsPage detailsPage = inventoryPage.goToProductDetails(scenario.productName());
        Assert.assertTrue(detailsPage.isLoaded(), "A página de detalhes não foi exibida.");
        return detailsPage;
    }
}