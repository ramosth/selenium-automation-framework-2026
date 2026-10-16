package com.test.automation.tests.cart;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.ProductDetailsPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: o carrinho é mantido ao navegar, ao recarregar a página e após logout/login.
 * (O Sauce Demo guarda o carrinho no navegador, no localStorage; o logout só apaga a sessão.)
 */
public class CartPersistenceTest extends BaseTest {

    /** Pré-condição: usuário padrão logado, com 2 produtos no carrinho. */
    private InventoryPage openInventoryWithProducts(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        inventoryPage.addProductToCart(Product.BACKPACK.getName());
        inventoryPage.addProductToCart(Product.BIKE_LIGHT.getName());
        test.info("Produtos adicionados: " + Product.BACKPACK + ", " + Product.BIKE_LIGHT);
        Assert.assertEquals(inventoryPage.header().getCartBadgeCount(), 2, "O carrinho deveria ter 2 itens.");
        return inventoryPage;
    }

    /** Confere que a lista de produtos mostra os 2 itens no carrinho. */
    private void checkCartKept(InventoryPage inventoryPage) {
        Assert.assertEquals(inventoryPage.header().getCartBadgeCount(), 2, "O contador deveria continuar em 2.");
        Assert.assertTrue(inventoryPage.isProductInCart(Product.BACKPACK.getName()),
                "A mochila deveria continuar no carrinho ('Remove').");
        Assert.assertTrue(inventoryPage.isProductInCart(Product.BIKE_LIGHT.getName()),
                "A luz de bicicleta deveria continuar no carrinho ('Remove').");
    }

    @Test
    public void testCartKeptWhenNavigating() {
        InventoryPage inventoryPage = openInventoryWithProducts("PER01 - Carrinho mantido ao navegar entre páginas");

        ProductDetailsPage detailsPage = inventoryPage.goToProductDetails(Product.ONESIE.getName());
        Assert.assertTrue(detailsPage.isLoaded(), "A página de detalhes não foi exibida.");
        Assert.assertEquals(detailsPage.header().getCartBadgeCount(), 2, "Nos detalhes, o contador deveria ser 2.");

        InventoryPage backPage = detailsPage.backToProducts();
        Assert.assertTrue(backPage.isLoaded(), "Não voltou para a página de produtos.");
        checkCartKept(backPage);
        test.pass("Carrinho mantido ao navegar até os detalhes e voltar.");
    }

    @Test
    public void testCartKeptAfterRefresh() {
        InventoryPage inventoryPage = openInventoryWithProducts("PER02 - Carrinho mantido ao recarregar a página");

        inventoryPage.refreshPage();
        InventoryPage reloadedPage = new InventoryPage(driver);
        Assert.assertTrue(reloadedPage.isLoaded(), "A página de produtos não carregou após recarregar.");
        checkCartKept(reloadedPage);
        test.pass("Carrinho mantido após recarregar a página.");
    }

    @Test
    public void testCartKeptAfterLogoutAndLogin() {
        InventoryPage inventoryPage = openInventoryWithProducts("PER03 - Carrinho mantido após logout e login");

        LoginPage loginPage = inventoryPage.header().logout();
        Assert.assertTrue(loginPage.isLoaded(), "Após o logout, deveria voltar para a tela de login.");

        InventoryPage loggedAgainPage = loginPage.loginAsStandardUser();
        Assert.assertTrue(loggedAgainPage.isLoaded(), "A página de produtos não foi exibida após o novo login.");
        checkCartKept(loggedAgainPage);

        CartPage cartPage = loggedAgainPage.header().openCart();
        Assert.assertEqualsNoOrder(cartPage.getItemNames().toArray(),
                List.of(Product.BACKPACK.getName(), Product.BIKE_LIGHT.getName()).toArray(),
                "Os itens do carrinho deveriam ser os mesmos de antes do logout.");
        test.pass("Carrinho mantido após logout e novo login.");
    }
}