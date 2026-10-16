package com.test.automation.tests.checkout;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.CheckoutInformationPage;
import com.test.automation.pages.CheckoutOverviewPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: desistir da compra sem perder o carrinho.
 *   Cancel nos dados do comprador (passo 1) -> volta ao carrinho
 *   Cancel no resumo do pedido (passo 2)    -> volta à lista de produtos
 *   Continue Shopping no carrinho           -> volta à lista de produtos
 */
public class CancelCheckoutTest extends BaseTest {

    /** Pré-condição: usuário padrão logado, com 2 produtos no carrinho. */
    private InventoryPage openInventoryWithProducts(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        inventoryPage.addProductToCart(Product.BACKPACK.getName());
        inventoryPage.addProductToCart(Product.BIKE_LIGHT.getName());
        test.info("Produtos adicionados: " + Product.BACKPACK + ", " + Product.BIKE_LIGHT);
        return inventoryPage;
    }

    @Test
    public void testCancelOnInformationStep() {
        InventoryPage inventoryPage = openInventoryWithProducts("CNL01 - Cancel nos dados do comprador volta ao carrinho");

        CheckoutInformationPage informationPage = inventoryPage.header().openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");

        CartPage cartPage = informationPage.cancel();
        Assert.assertTrue(cartPage.isLoaded(), "Cancel deveria voltar para o carrinho.");
        Assert.assertEqualsNoOrder(cartPage.getItemNames().toArray(),
                List.of(Product.BACKPACK.getName(), Product.BIKE_LIGHT.getName()).toArray(),
                "Os itens do carrinho deveriam continuar após cancelar.");
        test.pass("Cancel voltou ao carrinho, com os itens mantidos.");
    }

    @Test
    public void testCancelOnOverviewStep() {
        InventoryPage inventoryPage = openInventoryWithProducts("CNL02 - Cancel no resumo volta aos produtos");

        CheckoutInformationPage informationPage = inventoryPage.header().openCart().goToCheckout();
        Assert.assertTrue(informationPage.isLoaded(), "A tela de dados do checkout não foi exibida.");
        informationPage.fillInformation("Maria", "Silva", "20000-000");

        CheckoutOverviewPage overviewPage = informationPage.continueToOverview();
        Assert.assertTrue(overviewPage.isLoaded(), "A tela de resumo do pedido não foi exibida.");

        InventoryPage backPage = overviewPage.cancel();
        Assert.assertTrue(backPage.isLoaded(), "Cancel no resumo deveria voltar para a lista de produtos.");
        Assert.assertEquals(backPage.header().getCartBadgeCount(), 2, "O carrinho deveria continuar com 2 itens.");
        test.pass("Cancel no resumo voltou aos produtos, sem perder o carrinho.");
    }

    @Test
    public void testContinueShoppingKeepsCart() {
        InventoryPage inventoryPage = openInventoryWithProducts("CNL03 - Continue Shopping volta aos produtos");

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        InventoryPage backPage = cartPage.continueShopping();
        Assert.assertTrue(backPage.isLoaded(), "Continue Shopping deveria voltar para a lista de produtos.");
        Assert.assertEquals(backPage.header().getCartBadgeCount(), 2, "O carrinho deveria continuar com 2 itens.");
        Assert.assertTrue(backPage.isProductInCart(Product.BACKPACK.getName()),
                "O botão do produto deveria continuar mostrando 'Remove'.");
        test.pass("Continue Shopping voltou aos produtos, sem perder o carrinho.");
    }
}