package com.test.automation.tests.navigation;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.config.ConfigReader;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: menu lateral (HeaderComponent): abrir/fechar, All Items, About,
 * Logout (incluindo o bloqueio de acesso sem login) e Reset App State.
 */
public class SideMenuTest extends BaseTest {

    private static final String ABOUT_URL = "https://saucelabs.com/";
    private static final String MSG_LOGIN_REQUIRED =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    /** Pré-condição: usuário padrão logado na página de produtos. */
    private InventoryPage openInventory(String testName) {
        Log.info("Iniciando " + testName);
        test = ExtentReportManager.createTest(testName);

        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
        return inventoryPage;
    }

    @Test
    public void testOpenAndCloseMenu() {
        HeaderComponent header = openInventory("MNU01 - Abrir e fechar o menu lateral").header();

        header.openMenu();
        Assert.assertTrue(header.isMenuOpen(), "O menu deveria estar aberto.");

        header.closeMenu();
        Assert.assertFalse(header.isMenuOpen(), "O menu deveria estar fechado.");
        test.pass("O menu abre e fecha corretamente.");
    }

    @Test
    public void testAllItemsFromCart() {
        InventoryPage inventoryPage = openInventory("MNU02 - All Items a partir do carrinho");

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        InventoryPage backPage = cartPage.header().goToAllItems();
        Assert.assertTrue(backPage.isLoaded(), "'All Items' deveria levar para a página de produtos.");
        test.pass("'All Items' leva para a página de produtos.");
    }

    @Test
    public void testAboutOpensSauceLabsInSameTab() {
        HeaderComponent header = openInventory("MNU03 - About abre o site da Sauce Labs na mesma aba").header();

        // Nível 1, atributos do link: para onde aponta e se pede nova aba
        Assert.assertEquals(header.getAboutLinkUrl(), ABOUT_URL, "O link 'About' aponta para outro endereço.");
        Assert.assertEquals(header.getAboutLinkTarget(), "", "O link 'About' está configurado para abrir em outra aba.");

        // Nível 2, comportamento: clicar e conferir que continua na mesma aba, agora no site da Sauce Labs
        int tabsBefore = header.getOpenTabsCount();
        header.goToAbout();

        // URL e abas são perguntas sobre o navegador; o objeto header ainda tem o driver para respondê-las
        Assert.assertTrue(header.hasPageUrl("saucelabs.com"), "O clique em 'About' não levou ao site da Sauce Labs.");
        Assert.assertEquals(header.getOpenTabsCount(), tabsBefore, "O clique em 'About' abriu outra aba.");
        test.pass("'About' abre " + ABOUT_URL + " na mesma aba.");
    }

    @Test
    public void testLogoutBlocksAccessWithoutLogin() {
        InventoryPage inventoryPage = openInventory("MNU04 - Logout e bloqueio de acesso sem login");

        LoginPage loginPage = inventoryPage.header().logout();
        Assert.assertTrue(loginPage.isLoaded(), "Após o logout, deveria voltar para a tela de login.");

        // Tenta abrir a página de produtos direto pela URL, já sem estar logado
        String inventoryUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/") + "inventory.html";
        test.info("Acessando direto, sem login: " + inventoryUrl);
        driver.get(inventoryUrl);

        LoginPage blockedPage = new LoginPage(driver);
        Assert.assertTrue(blockedPage.isLoaded(), "Sem login, deveria continuar na tela de login.");
        Assert.assertEquals(blockedPage.getErrorMessage(), MSG_LOGIN_REQUIRED,
                "Mensagem de acesso sem login diferente da esperada.");
        test.pass("Logout realizado e acesso sem login bloqueado com a mensagem correta.");
    }

    @Test
    public void testResetAppStateClearsCart() {
        InventoryPage inventoryPage = openInventory("MNU05 - Reset App State esvazia o carrinho");
        HeaderComponent header = inventoryPage.header();

        inventoryPage.addProductToCart(Product.BACKPACK.getName());
        inventoryPage.addProductToCart(Product.BIKE_LIGHT.getName());
        test.info("Produtos adicionados: " + Product.BACKPACK + ", " + Product.BIKE_LIGHT);
        Assert.assertEquals(header.getCartBadgeCount(), 2, "O carrinho deveria ter 2 itens antes do reset.");

        header.resetAppState();
        Assert.assertEquals(header.getCartBadgeCount(), 0, "O contador do carrinho deveria zerar após o reset.");

        header.closeMenu(); // o reset deixa o menu aberto; fecha para não atrapalhar o próximo clique
        CartPage cartPage = header.openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");
        Assert.assertEquals(cartPage.getItemCount(), 0, "O carrinho deveria estar vazio após o reset.");
        test.pass("Reset App State esvaziou o carrinho.");
    }
}