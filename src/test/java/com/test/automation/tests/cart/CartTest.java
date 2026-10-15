package com.test.automation.tests.cart;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.test.automation.base.BaseTest;
import com.test.automation.dataproviders.CheckoutDataProvider;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;

/**
 * Requisito: adicionar produtos ao carrinho.
 * Dados: CheckoutData.xlsx, aba "Cart".
 */
public class CartTest extends BaseTest {

    @Test(dataProvider = "cartExcel", dataProviderClass = CheckoutDataProvider.class)
    public void testAddProductsToCart(String testId, String scenario, String products, String expectedCount) {
        Log.info("Iniciando " + testId + " - " + scenario);
        test = ExtentReportManager.createTest(testId + " - " + scenario);

        List<String> productList = Arrays.stream(products.split(";")).map(String::trim).toList();

        // Pré-condição: usuário logado na página de produtos
        InventoryPage inventoryPage = new LoginPage(driver).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");

        for (String product : productList) {
            inventoryPage.addProductToCart(product);
        }
        test.info("Produtos adicionados: " + productList);

        Assert.assertEquals(inventoryPage.header().getCartBadgeCount(), Integer.parseInt(expectedCount),
                "Quantidade no ícone do carrinho diferente da esperada.");

        CartPage cartPage = inventoryPage.header().openCart();
        Assert.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");
        Assert.assertEqualsNoOrder(cartPage.getItemNames().toArray(), productList.toArray(),
                "Os itens do carrinho são diferentes dos produtos adicionados.");
        test.pass("Carrinho com " + expectedCount + " item(ns), conforme esperado.");
    }
}
